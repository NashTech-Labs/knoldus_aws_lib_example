package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class S3BucketListResponse(buckets: Seq[String])

object S3BucketListResponse {

  implicit val format: Format[S3BucketListResponse] = Json.format

  def apply(json: JsValue): Either[String, S3BucketListResponse] =
    json.validate[S3BucketListResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3BucketListResponse $e")
    }
}
