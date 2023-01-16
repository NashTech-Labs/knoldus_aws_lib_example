package com.knoldus.aws.services.s3

import com.amazonaws.auth.{ AWSCredentialsProvider, DefaultAWSCredentialsProviderChain }
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
import com.knoldus.aws.utils.Constants.{ LOCALSTACK, OBJECT_UPLOADING_EXCEPTION }
import com.knoldus.common.aws.CredentialsLookup
import com.knoldus.s3.models._
import com.knoldus.s3.services.S3Service
import com.knoldus.s3.services.S3Service.buildAmazonS3Client
import com.typesafe.scalalogging.LazyLogging

import java.io.File
import scala.util.{ Failure, Success, Try }

class DataMigrationServiceImpl(s3config: Configuration) extends DataMigrationService with LazyLogging {

  implicit val s3Service: S3Service = new S3Service {
    override val config: Configuration = s3config

    override val amazonS3Client: AmazonS3 =
      if (s3config.s3Config.serviceEndpoint.equals(LOCALSTACK))
        AmazonS3ClientBuilder
          .standard()
          .withEndpointConfiguration(
            new EndpointConfiguration(s3config.s3Config.serviceEndpoint, s3config.awsConfig.awsRegion)
          )
          .withCredentials(new DefaultAWSCredentialsProviderChain())
          .withPathStyleAccessEnabled(true)
          .build()
      else {
        val credentials: AWSCredentialsProvider =
          CredentialsLookup.getCredentialsProvider(s3config.awsConfig.awsAccessKey, s3config.awsConfig.awsSecretKey)
        buildAmazonS3Client(s3config, credentials)
      }
  }

  override def uploadFileToS3(bucket: Bucket, file: File, key: String): Either[Throwable, PutObjectResult] =
    Try(s3Service.putObject(bucket, key, file)) match {
      case Failure(ex) =>
        logger.info(OBJECT_UPLOADING_EXCEPTION + s" ${ex.getMessage}")
        Left(ex)
      case Success(value) =>
        logger.info(s"Successfully uploaded the file ${file.getName} into the S3 bucket ${bucket.name}.")
        Right(value)
    }

  override def retrieveFile(bucket: Bucket, key: String, versionId: Option[String]): Either[Throwable, S3Object] =
    s3Service.getS3Object(bucket, key, versionId)

  override def copyFile(
    sourceBucketName: String,
    sourceKey: String,
    destinationBucketName: String,
    destinationKey: String
  ): Either[Throwable, PutObjectResult] =
    Try(s3Service.copyS3Object(sourceBucketName, sourceKey, destinationBucketName, destinationKey)) match {
      case Failure(exception) => Left(exception)
      case Success(putObjectResult) => Right(putObjectResult)
    }

  override def deleteFile(bucket: Bucket, key: String): Either[Throwable, DeletedObject] =
    Try(s3Service.deleteObject(bucket, key)) match {
      case Failure(exception) =>
        println(s"\n\n ${exception.getMessage}")
        Left(exception)
      case Success(deletedObject) => Right(deletedObject)
    }

  override def getAllObjects(bucket: Bucket, prefix: String): Either[Throwable, Seq[S3ObjectSummary]] = {
    println(s"\n\nprefix: $prefix\n\n")
    Try(s3Service.listDirAndObjectWithPrefix(bucket, prefix)) match {
      case Failure(ex) => Left(ex)
      case Success(value) => Right(value.flatMap(_.toSeq))
    }
  }

  override def getAllObjectSummaries(bucket: Bucket, prefix: String): List[S3ObjectSummary] =
    s3Service.objectSummaries(bucket, prefix).toList
}
