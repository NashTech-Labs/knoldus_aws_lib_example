package com.knoldus.aws.services.s3

import com.amazonaws.services.s3.AmazonS3
import com.knoldus.aws.utils.Constants.OBJECT_RETRIEVAL_EXCEPTION
import com.knoldus.aws.utils.S3ClientBuilder.s3ClientBuilder
import com.knoldus.s3.models._
import com.knoldus.s3.services.S3Service
import com.typesafe.scalalogging.LazyLogging

import java.io.File
import scala.util.{ Failure, Success, Try }

class DataMigrationServiceImpl(s3config: Configuration) extends DataMigrationService with LazyLogging {

  implicit val s3Service: S3Service = new S3Service {
    override val config: Configuration = s3config

    override val amazonS3Client: AmazonS3 = s3ClientBuilder(s3config)
  }

  override def uploadFileToS3(bucket: Bucket, file: File, key: String): Either[Throwable, PutObjectResult] =
    Try(s3Service.putObject(bucket, key, file)) match {
      case Failure(exception) =>
        logger.info(s"Failed to upload object ${file.getName} in S3 bucket ${bucket.name}: ${exception.getMessage}")
        Left(exception)
      case Success(putObjectResult) =>
        logger.info(s"Successfully uploaded the file ${file.getName} into the S3 bucket ${bucket.name}.")
        Right(putObjectResult)
    }

  override def retrieveFile(bucket: Bucket, key: String, versionId: Option[String]): Either[String, S3ObjectSummary] =
    s3Service.getS3Object(bucket, key, versionId) match {
      case Left(exception) =>
        logger.info(
          s"Failed to retrieve object from the bucket ${bucket.name} with the key $key : ${exception.getMessage}"
        )
        Left(OBJECT_RETRIEVAL_EXCEPTION)
      case Right(s3Object) =>
        getAllObjectSummaries(s3Object.bucket, s3Object.key).headOption match {
          case Some(s3ObjectSummary) =>
            logger.info(s"Successfully retrieved object summary from the bucket ${bucket.name} with the key $key")
            Right(s3ObjectSummary)
          case None =>
            logger.info(s"Failed to retrieve object summary from the bucket ${bucket.name} with the key $key")
            Left(OBJECT_RETRIEVAL_EXCEPTION)
        }
    }

  override def copyFile(
    sourceBucketName: String,
    sourceKey: String,
    destinationBucketName: String,
    destinationKey: String
  ): Either[Throwable, PutObjectResult] =
    Try(s3Service.copyS3Object(sourceBucketName, sourceKey, destinationBucketName, destinationKey)) match {
      case Failure(exception) =>
        logger.info(
          s"Failed to copy object from the bucket: $sourceBucketName into the bucket: $destinationBucketName : ${exception.getMessage}"
        )
        Left(exception)
      case Success(putObjectResult) =>
        logger.info(
          s"Successfully copied object from the bucket: $sourceBucketName into the bucket: $destinationBucketName."
        )
        Right(putObjectResult)
    }

  override def deleteFile(bucket: Bucket, key: String): Either[Throwable, DeletedObject] =
    Try(s3Service.deleteObject(bucket, key)) match {
      case Failure(exception) =>
        logger.info(s"Failed to delete s3 object from bucket ${bucket.name}: ${exception.getMessage}")
        Left(exception)
      case Success(deletedObject) =>
        logger.info(s"Successfully deleted the s3 object from bucket: ${bucket.name}")
        Right(deletedObject)
    }

  override def getAllObjects(bucket: Bucket, prefix: String): Either[Throwable, Seq[S3ObjectSummary]] =
    Try(s3Service.listDirAndObjectWithPrefix(bucket, prefix)) match {
      case Failure(exception) =>
        logger.info(s"Failed to retrieve all S3 objects from bucket ${bucket.name}: ${exception.getMessage}")
        Left(exception)
      case Success(objectSummaries) =>
        logger.info(s"Successfully retrieved all S3 objects from bucket: ${bucket.name}")
        Right(objectSummaries.flatMap(_.toSeq))
    }

  override def getAllObjectSummaries(bucket: Bucket, prefix: String): List[S3ObjectSummary] = {
    logger.info(s"Retrieving all the object summaries from the bucket ${bucket.name} with the prefix $prefix")
    s3Service.objectSummaries(bucket, prefix).toList
  }
}
