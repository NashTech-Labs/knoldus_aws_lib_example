package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class S3Bucket(bucketName: String)

object S3Bucket {

  implicit val format: Format[S3Bucket] = Json.format

  def apply(json: JsValue): Either[String, S3Bucket] =
    json.validate[S3Bucket] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3Bucket $e")
    }
}
