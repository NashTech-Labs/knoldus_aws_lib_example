package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class RetrieveObjectRequest(bucketName: String, fileName: String, key: String, versionId: Option[String])

object RetrieveObjectRequest {

  implicit val format: Format[RetrieveObjectRequest] = Json.format

  def apply(json: JsValue): Either[String, RetrieveObjectRequest] =
    json.validate[RetrieveObjectRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize RetrieveObjectRequest $e")
    }
}
