package com.knoldus.aws.routes.s3

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import com.knoldus.aws.models.s3._
import com.knoldus.aws.services.s3.S3BucketService
import com.knoldus.aws.utils.Constants._
import com.knoldus.aws.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging
import spray.json.enrichAny

class S3BucketAPIImpl(s3BucketService: S3BucketService) extends S3BucketAPI with JsonSupport with LazyLogging {

  val routes: Route = createS3Bucket ~ searchS3Bucket ~ listAllBuckets ~ deleteS3Bucket() ~ retrieveAllBucketKeys

  implicit val noSuchElementExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound, e.getMessage)
  }

  override def createS3Bucket: Route =
    path("bucket" / "create") {
      pathEnd {
        (post & entity(as[S3Bucket])) { bucketCreationRequest =>
          logger.info("Making request for S3 bucket creation")
          s3BucketService.searchS3Bucket(bucketCreationRequest.bucketName) match {
            case Some(_) =>
              complete(
                HttpResponse(
                  StatusCodes.BadRequest,
                  entity = HttpEntity(ContentTypes.`application/json`, BUCKET_ALREADY_EXISTS)
                )
              )
            case None =>
              s3BucketService.createS3Bucket(bucketCreationRequest.bucketName) match {
                case Left(_) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(ContentTypes.`application/json`, BUCKET_CREATION_EXCEPTION)
                    )
                  )
                case Right(_) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, BUCKET_CREATED)
                    )
                  )
              }
          }
        }
      }
    }

  override def deleteS3Bucket(): Route =
    path("bucket" / "delete") {
      pathEnd {
        (delete & entity(as[S3Bucket])) { bucketDeletionRequest =>
          logger.info("Making request for S3 bucket deletion")
          s3BucketService.searchS3Bucket(bucketDeletionRequest.bucketName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                )
              )
            case Some(bucket) =>
              s3BucketService.deleteS3Bucket(bucket) match {
                case Left(_) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(ContentTypes.`application/json`, BUCKET_DELETION_EXCEPTION)
                    )
                  )
                case Right(msg) =>
                  complete(
                    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, msg))
                  )
              }
          }
        }
      }
    }

  override def searchS3Bucket: Route =
    path("bucket" / "search") {
      pathEnd {
        (get & entity(as[S3Bucket])) { searchBucketRequest =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for searching the S3 bucket.")
            s3BucketService.searchS3Bucket(searchBucketRequest.bucketName) match {
              case None =>
                complete(
                  HttpResponse(
                    StatusCodes.NotFound,
                    entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                  )
                )
              case Some(bucket) =>
                complete(
                  HttpResponse(
                    StatusCodes.OK,
                    entity =
                      HttpEntity(ContentTypes.`application/json`, S3BucketResponse(bucket.name).toJson.prettyPrint)
                  )
                )
            }
          }
        }
      }
    }

  override def listAllBuckets: Route =
    path("bucket" / "allBuckets") {
      pathEnd {
        get {
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for getting all the S3 buckets.")
            s3BucketService.listAllBuckets match {
              case Left(_) =>
                complete(
                  HttpResponse(
                    StatusCodes.InternalServerError,
                    entity = HttpEntity(ContentTypes.`application/json`, BUCKET_LISTING_EXCEPTION)
                  )
                )
              case Right(bucketSeq) =>
                bucketSeq match {
                  case None =>
                    complete(
                      HttpResponse(StatusCodes.NoContent)
                    )
                  case Some(buckets) =>
                    val bucketResponse = S3BucketListResponse(buckets.map(_.name))
                    complete(
                      HttpResponse(
                        StatusCodes.OK,
                        entity = HttpEntity(ContentTypes.`application/json`, bucketResponse.toJson.prettyPrint)
                      )
                    )
                }
            }
          }
        }
      }
    }

  override def retrieveAllBucketKeys: Route =
    path("bucket" / "allKeys") {
      pathEnd {
        (get & entity(as[RetrieveBucketKeysRequest])) { retrieveKeysRequest =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for retrieving all the S3 bucket keys.")
            s3BucketService.searchS3Bucket(retrieveKeysRequest.bucketName) match {
              case None =>
                complete(
                  HttpResponse(
                    StatusCodes.NotFound,
                    entity = HttpEntity(ContentTypes.`application/json`, BUCKET_NOT_FOUND)
                  )
                )
              case Some(bucket) =>
                s3BucketService.retrieveBucketKeys(bucket, retrieveKeysRequest.prefix) match {
                  case Left(_) =>
                    complete(
                      HttpResponse(
                        StatusCodes.InternalServerError,
                        entity = HttpEntity(ContentTypes.`application/json`, RETRIEVING_ALL_KEYS_EXCEPTION)
                      )
                    )
                  case Right(keys) =>
                    if (keys.isEmpty)
                      complete(
                        HttpResponse(StatusCodes.NoContent)
                      )
                    else
                      complete(
                        HttpResponse(
                          StatusCodes.OK,
                          entity =
                            HttpEntity(ContentTypes.`application/json`, S3BucketKeysResponse(keys).toJson.prettyPrint)
                        )
                      )
                }
            }
          }
        }
      }
    }
}
