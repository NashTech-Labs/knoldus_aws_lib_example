package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class CopyObjectRequest(
  sourceBucketName: String,
  sourceKey: String,
  destinationBucketName: String,
  destinationKey: String
)

object CopyObjectRequest {

  implicit val format: Format[CopyObjectRequest] = Json.format

  def apply(json: JsValue): Either[String, CopyObjectRequest] =
    json.validate[CopyObjectRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize CopyObjectRequest $e")
    }
}
