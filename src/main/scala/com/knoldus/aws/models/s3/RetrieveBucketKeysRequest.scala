package com.knoldus.aws.models.s3

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class RetrieveBucketKeysRequest(bucketName: String, prefix: Option[String])

object RetrieveBucketKeysRequest {

  implicit val format: Format[RetrieveBucketKeysRequest] = Json.format

  def apply(json: JsValue): Either[String, RetrieveBucketKeysRequest] =
    json.validate[RetrieveBucketKeysRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize RetrieveBucketKeysRequest $e")
    }
}
