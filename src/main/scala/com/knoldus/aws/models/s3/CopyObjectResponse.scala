package com.knoldus.aws.models.s3

import com.knoldus.s3.models.Bucket
import org.joda.time.DateTime
import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class CopyObjectResponse(
  destinationBucketName: String,
  key: String,
  versionId: String,
  expirationTime: String
)

object CopyObjectResponse {

  implicit val format: Format[CopyObjectResponse] = Json.format

  def apply(json: JsValue): Either[String, CopyObjectResponse] =
    json.validate[CopyObjectResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize CopyObjectResponse $e")
    }
}
