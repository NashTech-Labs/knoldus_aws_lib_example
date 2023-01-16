package com.knoldus.aws.services.s3

import com.amazonaws.auth.{ AWSCredentialsProvider, DefaultAWSCredentialsProviderChain }
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
import com.knoldus.aws.utils.Constants.{ BUCKET_DELETED, LOCALSTACK }
import com.knoldus.common.aws.CredentialsLookup
import com.knoldus.s3.models.{ Bucket, Configuration }
import com.knoldus.s3.services.S3Service
import com.knoldus.s3.services.S3Service.buildAmazonS3Client

import scala.util.{ Failure, Success, Try }

class S3BucketServiceImpl(s3config: Configuration) extends S3BucketService {

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

  override def createS3Bucket(bucketName: String): Either[Throwable, Bucket] =
    Try(s3Service.createBucket(bucketName, Some(s3Service.config.awsConfig.awsRegion))) match {
      case Failure(ex) =>
        Left(ex)
      case Success(bucket) =>
        Right(bucket)
    }

  override def deleteS3Bucket(bucket: Bucket): Either[Throwable, String] =
    Try {
      bucket.objectSummaries().toList.foreach { obj =>
        s3Service.amazonS3Client.deleteObject(bucket.name, obj.getKey)
      }
      bucket.destroy()
    } match {
      case Failure(ex) => Left(ex)
      case Success(_) => Right(BUCKET_DELETED)
    }

  override def listAllBuckets: Either[Throwable, Option[Seq[Bucket]]] =
    Try(s3Service.getAllBuckets) match {
      case Failure(ex) => Left(ex)
      case Success(bucketSeq) =>
        if (bucketSeq.isEmpty) Right(None)
        else Right(Some(bucketSeq))
    }

  override def searchS3Bucket(name: String): Option[Bucket] =
    s3Service.getBucketByName(name)

  override def retrieveBucketKeys(bucket: Bucket, prefix: Option[String]): Either[Throwable, Seq[String]] = {
    val tryToRetrieveBucketKeys = prefix match {
      case Some(somePrefix) => Try(s3Service.keys(bucket, somePrefix))
      case None => Try(s3Service.keys(bucket))
    }
    tryToRetrieveBucketKeys match {
      case Failure(ex) =>
        println(s"\n\n ${ex.getMessage}")
        Left(ex)
      case Success(keys) => Right(keys)
    }
  }
}
