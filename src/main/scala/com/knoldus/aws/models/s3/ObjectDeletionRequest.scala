package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class ObjectDeletionRequest(bucketName: String, key: String)

object ObjectDeletionRequest {

  implicit val format: Format[ObjectDeletionRequest] = Json.format

  def apply(json: JsValue): Either[String, ObjectDeletionRequest] =
    json.validate[ObjectDeletionRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize ObjectDeletionRequest $e")
    }
}
