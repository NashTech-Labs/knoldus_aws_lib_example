package com.knoldus.aws.routes.s3

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{ entity, _ }
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import com.knoldus.aws.bootstrap.DriverApp.actorSystem
import com.knoldus.aws.models.s3._
import com.knoldus.aws.services.s3.{ DataMigrationServiceImpl, S3BucketServiceImpl }
import com.knoldus.aws.utils.Constants._
import com.knoldus.aws.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging
import spray.json.enrichAny

import java.io.File
import java.util.UUID

class DataMigrationAPIImpl(dataMigrationServiceImpl: DataMigrationServiceImpl, s3BucketServiceImpl: S3BucketServiceImpl)
    extends DataMigrationAPI
    with JsonSupport
    with LazyLogging {

  val routes: Route = uploadFileToS3 ~ retrieveFile ~ copyFile ~ deleteFile() ~ getAllObjects

  implicit val noSuchElementExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound, e.getMessage)
  }

  override def uploadFileToS3: Route =
    path("bucket" / "upload" / "file") {
      (post & entity(as[Multipart.FormData])) { _ =>
        formField("bucketName", "key") { (bucketName, key) =>
          storeUploadedFile("file", tempDestination) {
            case (_, file: File) =>
              s3BucketServiceImpl.searchS3Bucket(bucketName) match {
                case None =>
                  complete(
                    HttpResponse(
                      StatusCodes.NotFound,
                      entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                    )
                  )
                case Some(bucket) =>
                  dataMigrationServiceImpl.uploadFileToS3(bucket, file, key) match {
                    case Left(_) =>
                      complete(
                        HttpResponse(
                          StatusCodes.InternalServerError,
                          entity = HttpEntity(ContentTypes.`application/json`, OBJECT_UPLOADING_EXCEPTION)
                        )
                      )
                    case Right(_) =>
                      complete(
                        HttpResponse(
                          StatusCodes.OK,
                          entity = HttpEntity(ContentTypes.`application/json`, OBJECT_UPLOADED)
                        )
                      )
                  }
              }
          }
        }
      }
    }

  private def tempDestination(fileInfo: FileInfo): File =
    File.createTempFile(UUID.randomUUID().toString, UUID.nameUUIDFromBytes(fileInfo.fileName.getBytes).toString)

  override def retrieveFile: Route =
    path("bucket" / "retrieveObject") {
      pathEnd {
        (get & entity(as[RetrieveObjectRequest])) { fileRetrieveRequest =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for retrieving object from the S3 bucket.")
            s3BucketServiceImpl.searchS3Bucket(fileRetrieveRequest.bucketName) match {
              case None =>
                complete(
                  HttpResponse(
                    StatusCodes.NotFound,
                    entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                  )
                )
              case Some(bucket) =>
                dataMigrationServiceImpl.retrieveFile(
                  bucket,
                  fileRetrieveRequest.key,
                  fileRetrieveRequest.versionId
                ) match {
                  case Left(msg) =>
                    complete(
                      HttpResponse(
                        StatusCodes.NotFound,
                        entity = HttpEntity(ContentTypes.`application/json`, msg)
                      )
                    )
                  case Right(s3Object) =>
                    val objectSummary = S3ObjectSummariesResponse(
                      s3Object.bucket.name,
                      s3Object.getKey,
                      s3Object.getSize,
                      s3Object.getStorageClass,
                      s3Object.getETag,
                      s3Object.getLastModified.toString,
                      s3Object.getOwner.getDisplayName
                    )
                    complete(
                      HttpResponse(
                        StatusCodes.OK,
                        entity = HttpEntity(
                          ContentTypes.`application/json`,
                          objectSummary.toJson.prettyPrint
                        )
                      )
                    )
                }
            }
          }
        }
      }
    }

  override def copyFile: Route =
    path("bucket" / "copyObject") {
      pathEnd {
        (post & entity(as[CopyObjectRequest])) { copyObjectRequest =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for copying object from the S3 bucket.")
            dataMigrationServiceImpl.copyFile(
              copyObjectRequest.sourceBucketName,
              copyObjectRequest.sourceKey,
              copyObjectRequest.destinationBucketName,
              copyObjectRequest.destinationKey
            ) match {
              case Left(_) =>
                complete(
                  HttpResponse(
                    StatusCodes.InternalServerError,
                    entity = HttpEntity(ContentTypes.`application/json`, OBJECT_COPYING_EXCEPTION)
                  )
                )
              case Right(putObjectResult) =>
                val copyObjectResponse = CopyObjectResponse(
                  putObjectResult.bucket.name,
                  putObjectResult.key,
                  putObjectResult.expirationTime.toString
                )
                complete(
                  HttpResponse(
                    StatusCodes.OK,
                    entity = HttpEntity(ContentTypes.`application/json`, copyObjectResponse.toJson.prettyPrint)
                  )
                )
            }
          }
        }
      }
    }

  override def deleteFile(): Route =
    path("bucket" / "deleteObject") {
      pathEnd {
        (delete & entity(as[ObjectDeletionRequest])) { objectDeletionRequest =>
          logger.info("Making request for S3 object deletion")
          s3BucketServiceImpl.searchS3Bucket(objectDeletionRequest.bucketName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                )
              )
            case Some(bucket) =>
              if (objectDeletionRequest.key.isEmpty) {
                dataMigrationServiceImpl.getAllObjectSummaries(bucket, objectDeletionRequest.key).map { obj =>
                  dataMigrationServiceImpl.deleteFile(obj.bucket, obj.getKey)
                }
                complete(
                  HttpResponse(
                    StatusCodes.OK,
                    entity = HttpEntity(ContentTypes.`application/json`, ALL_OBJECTS_DELETED)
                  )
                )
              } else
                s3BucketServiceImpl.retrieveBucketKeys(bucket, Some(objectDeletionRequest.key)).toOption match {
                  case None =>
                    complete(
                      HttpResponse(
                        StatusCodes.NotFound,
                        entity = HttpEntity(ContentTypes.`application/json`, KEY_NOT_FOUND)
                      )
                    )
                  case Some(keys) =>
                    if (keys.isEmpty)
                      complete(
                        HttpResponse(
                          StatusCodes.NotFound,
                          entity = HttpEntity(ContentTypes.`application/json`, KEY_NOT_FOUND)
                        )
                      )
                    else
                      dataMigrationServiceImpl.deleteFile(bucket, objectDeletionRequest.key) match {
                        case Left(_) =>
                          complete(
                            HttpResponse(
                              StatusCodes.InternalServerError,
                              entity = HttpEntity(ContentTypes.`application/json`, OBJECT_DELETION_EXCEPTION)
                            )
                          )
                        case Right(_) =>
                          complete(
                            HttpResponse(
                              StatusCodes.OK,
                              entity = HttpEntity(ContentTypes.`application/json`, OBJECT_DELETED)
                            )
                          )
                      }
                }
          }
        }
      }
    }

  override def getAllObjects: Route =
    path("bucket" / "getAllObjects") {
      pathEnd {
        (get & entity(as[RetrieveObjectSummariesRequest])) { retrieveObjectSummaries =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for getting all the objects in the S3 bucket")
            s3BucketServiceImpl.searchS3Bucket(retrieveObjectSummaries.bucketName) match {
              case None =>
                complete(
                  HttpResponse(
                    StatusCodes.NotFound,
                    entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                  )
                )
              case Some(bucket) =>
                dataMigrationServiceImpl.getAllObjects(bucket, retrieveObjectSummaries.prefix) match {
                  case Left(_) =>
                    complete(
                      HttpResponse(
                        StatusCodes.InternalServerError,
                        entity = HttpEntity(ContentTypes.`application/json`, RETRIEVING_ALL_OBJECTS_EXCEPTION)
                      )
                    )
                  case Right(objSummaries) =>
                    if (objSummaries.isEmpty)
                      complete(
                        HttpResponse(StatusCodes.NoContent)
                      )
                    else {
                      val objResponse = objSummaries.map { obj =>
                        S3ObjectSummariesResponse(
                          obj.bucket.name,
                          obj.getKey,
                          obj.getSize,
                          obj.getStorageClass,
                          obj.getETag,
                          obj.getLastModified.toString,
                          obj.getOwner.getDisplayName
                        )
                      }
                      complete(
                        HttpResponse(
                          StatusCodes.OK,
                          entity = HttpEntity(ContentTypes.`application/json`, objResponse.toJson.prettyPrint)
                        )
                      )
                    }
                }
            }
          }
        }
      }
    }
}
