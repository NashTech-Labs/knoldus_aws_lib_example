package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class QueueListResponse(queues: Seq[String])

object QueueListResponse {

  implicit val format: Format[QueueListResponse] = Json.format

  def apply(json: JsValue): Either[String, QueueListResponse] =
    json.validate[QueueListResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize S3BucketListResponse $e")
    }
}
