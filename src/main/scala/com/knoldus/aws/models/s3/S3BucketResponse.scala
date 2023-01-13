package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class S3BucketResponse(bucketName: String)

object S3BucketResponse {

  implicit val format: Format[S3BucketResponse] = Json.format

  def apply(json: JsValue): Either[String, S3BucketResponse] =
    json.validate[S3BucketResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3BucketResponse $e")
    }
}
