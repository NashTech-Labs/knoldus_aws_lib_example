package com.knoldus.aws.services.s3

import com.amazonaws.services.s3.AmazonS3
import com.knoldus.aws.utils.S3ClientBuilder.s3ClientBuilder
import com.knoldus.aws.utils.Constants.BUCKET_DELETED
import com.knoldus.s3.models.{ Bucket, Configuration }
import com.knoldus.s3.services.S3Service
import com.typesafe.scalalogging.LazyLogging

import scala.util.{ Failure, Success, Try }

class S3BucketServiceImpl(s3config: Configuration) extends S3BucketService with LazyLogging {

  implicit val s3Service: S3Service = new S3Service {
    override val config: Configuration = s3config

    override val amazonS3Client: AmazonS3 = s3ClientBuilder(s3config)
  }

  override def createS3Bucket(bucketName: String): Either[Throwable, Bucket] =
    Try(s3Service.createBucket(bucketName, Some(s3Service.config.awsConfig.awsRegion))) match {
      case Failure(exception) =>
        logger.info(s"Failed to create S3 bucket $bucketName: ${exception.getMessage}")
        Left(exception)
      case Success(bucket) =>
        logger.info(s"Successfully created S3 bucket: ${bucket.name}")
        Right(bucket)
    }

  override def deleteS3Bucket(bucket: Bucket): Either[Throwable, String] =
    Try {
      bucket.objectSummaries().toList.foreach { obj =>
        s3Service.amazonS3Client.deleteObject(bucket.name, obj.getKey)
      }
      bucket.destroy()
    } match {
      case Failure(exception) =>
        logger.info(s"Failed to delete S3 bucket ${bucket.name}: ${exception.getMessage}")
        Left(exception)
      case Success(_) =>
        logger.info(s"Successfully deleted S3 bucket: ${bucket.name}")
        Right(BUCKET_DELETED)
    }

  override def listAllBuckets: Either[Throwable, Option[Seq[Bucket]]] =
    Try(s3Service.getAllBuckets) match {
      case Failure(exception) =>
        logger.info(s"Failed to retrieve all S3 buckets: ${exception.getMessage}")
        Left(exception)
      case Success(bucketSeq) =>
        logger.info(s"Successfully retrieved all the S3 buckets.")
        if (bucketSeq.isEmpty) Right(None)
        else Right(Some(bucketSeq))
    }

  override def searchS3Bucket(name: String): Option[Bucket] = {
    logger.info(s"Searching for bucket: $name in the list of S3 buckets.")
    s3Service.getBucketByName(name)
  }

  override def retrieveBucketKeys(bucket: Bucket, prefix: Option[String]): Either[Throwable, Seq[String]] = {
    val tryToRetrieveBucketKeys = prefix match {
      case Some(somePrefix) => Try(s3Service.keys(bucket, somePrefix))
      case None => Try(s3Service.keys(bucket))
    }
    tryToRetrieveBucketKeys match {
      case Failure(exception) =>
        logger.info(s"Failed to retrieve keys for S3 bucket ${bucket.name}: ${exception.getMessage}")
        Left(exception)
      case Success(keys) =>
        logger.info(s"Successfully retrieved keys for S3 bucket: ${bucket.name}")
        Right(keys)
    }
  }
}
