package com.knoldus.aws.routes.s3

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import akka.util.ByteString
import com.knoldus.aws.bootstrap.DriverApp.actorSystem
import com.knoldus.aws.models.s3.{ CopyObjectRequest, FileRetrieveRequest, ObjectDeletionRequest }
import com.knoldus.aws.services.s3.DataMigrationServiceImpl
import com.knoldus.aws.utils.Constants.FILE_UPLOADED
import com.knoldus.aws.utils.JsonSupport
import com.knoldus.s3.models.Bucket
import com.typesafe.scalalogging.LazyLogging

import java.io.File
import java.util.UUID

class DataMigrationAPIImpl(dataMigrationServiceImpl: DataMigrationServiceImpl)
    extends DataMigrationAPI
    with JsonSupport
    with LazyLogging {

  val routes: Route = retrieveFile ~ copyFile ~ deleteFile()

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
              implicit val bucket: Bucket = Bucket(bucketName)
              dataMigrationServiceImpl.uploadFileToS3(file, key) match {
                case Left(error) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(
                        ContentTypes.`application/json`,
                        ByteString(s"File could not be uploaded to S3 Bucket : ${error.getMessage}")
                      )
                    )
                  )
                case Right(_) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, ByteString(FILE_UPLOADED))
                    )
                  )
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
        (get & entity(as[FileRetrieveRequest])) { fileRetrieveRequest =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for retrieving object from the S3 bucket.")
            implicit val bucket: Bucket = Bucket(fileRetrieveRequest.bucketName)
            dataMigrationServiceImpl.retrieveFile(fileRetrieveRequest.key, fileRetrieveRequest.versionId) match {
              case Left(ex) =>
                complete(
                  HttpResponse(
                    StatusCodes.NotFound,
                    entity =
                      HttpEntity(ContentTypes.`application/json`, ByteString(s"S3 Object not found. ${ex.getMessage}"))
                  )
                )
              case Right(s3Object) =>
                complete(
                  HttpResponse(
                    StatusCodes.OK,
                    entity = HttpEntity(ContentTypes.`application/json`, ByteString(s3Object.toString))
                  )
                )
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
              case Left(ex) =>
                complete(
                  HttpResponse(
                    StatusCodes.NotFound,
                    entity = HttpEntity(
                      ContentTypes.`application/json`,
                      ByteString(s"Unable to copy S3 object: ${ex.getMessage}")
                    )
                  )
                )
              case Right(putObjectResult) =>
                complete(
                  HttpResponse(
                    StatusCodes.OK,
                    entity = HttpEntity(ContentTypes.`application/json`, ByteString(putObjectResult.toString))
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
          implicit val bucket: Bucket = Bucket(objectDeletionRequest.bucketName)
          dataMigrationServiceImpl.deleteFile(objectDeletionRequest.key) match {
            case Left(ex) =>
              complete(
                HttpResponse(
                  StatusCodes.InternalServerError,
                  entity = HttpEntity(
                    ContentTypes.`application/json`,
                    ByteString(s"Unable to delete S3 object: ${ex.getMessage}")
                  )
                )
              )
            case Right(deletedObject) =>
              complete(
                HttpResponse(
                  StatusCodes.OK,
                  entity = HttpEntity(ContentTypes.`application/json`, ByteString(deletedObject.toString))
                )
              )
          }
        }
      }
    }
}
