package com.knoldus.aws.services.s3

import com.knoldus.s3.models.{ Bucket, DeletedObject, PutObjectResult, S3Object, S3ObjectSummary }

import java.io.File

trait DataMigrationService {

  def uploadFileToS3(bucket: Bucket, file: File, key: String): Either[Throwable, PutObjectResult]

  def retrieveFile(bucket: Bucket, key: String, versionId: Option[String]): Either[Throwable, S3Object]

  def copyFile(
    sourceBucketName: String,
    sourceKey: String,
    destinationBucketName: String,
    destinationKey: String
  ): Either[Throwable, PutObjectResult]

  def deleteFile(bucket: Bucket, key: String): Either[Throwable, DeletedObject]

  def getAllObjects(bucket: Bucket, prefix: String): Either[Throwable, Seq[S3ObjectSummary]]

}
