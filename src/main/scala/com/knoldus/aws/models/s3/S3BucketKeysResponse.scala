package com.knoldus.aws.models.s3

import play.api.libs.json.{Format, JsSuccess, JsValue, Json}

case class S3BucketKeysResponse(keys: Seq[String])

object S3BucketKeysResponse {

  implicit val format: Format[S3BucketKeysResponse] = Json.format

  def apply(json: JsValue): Either[String, S3BucketKeysResponse] =
    json.validate[S3BucketKeysResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3BucketKeysResponse $e")
    }
}
