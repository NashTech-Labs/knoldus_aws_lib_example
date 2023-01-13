package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class S3ObjectResponse(bucketName: String, key: String)

object S3ObjectResponse {

  implicit val format: Format[S3ObjectResponse] = Json.format

  def apply(json: JsValue): Either[String, S3ObjectResponse] =
    json.validate[S3ObjectResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3ObjectResponse $e")
    }
}
