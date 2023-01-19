package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class RetrieveObjectSummariesRequest(bucketName: String, prefix: String)

object RetrieveObjectSummariesRequest {

  implicit val format: Format[RetrieveObjectSummariesRequest] = Json.format

  def apply(json: JsValue): Either[String, RetrieveObjectSummariesRequest] =
    json.validate[RetrieveObjectSummariesRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize RetrieveObjectSummariesRequest $e")
    }
}
