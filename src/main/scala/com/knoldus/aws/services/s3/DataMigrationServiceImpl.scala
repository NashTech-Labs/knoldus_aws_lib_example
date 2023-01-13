package com.knoldus.aws.services.s3

import com.knoldus.s3.models.{ Bucket, DeletedObject, PutObjectResult, S3Object, S3ObjectSummary }
import com.knoldus.s3.services.S3Service

import java.io.File
import scala.util.{ Failure, Success, Try }

class DataMigrationServiceImpl extends DataMigrationService {

  implicit val s3Service: S3Service = S3Service

  override def uploadFileToS3(bucket: Bucket, file: File, key: String): Either[Throwable, PutObjectResult] =
    Try(s3Service.putObject(bucket, key, file)) match {
      case Failure(exception) => Left(exception)
      case Success(putObjectResult) => Right(putObjectResult)
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
      case Failure(exception) => Left(exception)
      case Success(deletedObject) => Right(deletedObject)
    }

  override def getAllObjects(bucket: Bucket, prefix: String): Either[Throwable, Seq[S3ObjectSummary]] = {
    val s3ObjectSummaryEither = Try(s3Service.listDirAndObjectWithPrefix(bucket, prefix))
    s3ObjectSummaryEither match {
      case Failure(ex) => Left(ex)
      case Success(value) => Right(value.flatMap(_.toSeq))
    }
  }
}
