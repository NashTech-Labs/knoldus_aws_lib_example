package com.knoldus.aws.services.s3

import com.knoldus.s3.models.{ Bucket, S3ObjectSummary }

trait S3BucketService {

  def createS3Bucket(bucketName: String): Either[Throwable, Bucket]

  def deleteS3Bucket(bucket: Bucket): Either[Throwable, String]

  def listAllBuckets: Either[Throwable, Option[Seq[Bucket]]]

  def searchS3Bucket(name: String): Option[Bucket]

  def retrieveBucketKeys(bucket: Bucket, prefix: Option[String]): Either[Throwable, Seq[String]]
}
