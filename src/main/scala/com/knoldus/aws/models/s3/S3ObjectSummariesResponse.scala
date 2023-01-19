package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class S3ObjectSummariesResponse(
  bucketName: String,
  key: String,
  size: Long,
  storageClass: String,
  eTag: String,
  lastModified: String,
  owner: String
)

object S3ObjectSummariesResponse {

  implicit val format: Format[S3ObjectSummariesResponse] = Json.format

  def apply(json: JsValue): Either[String, S3ObjectSummariesResponse] =
    json.validate[S3ObjectSummariesResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3ObjectSummariesResponse $e")
    }
}
