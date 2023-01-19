package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class DeleteQueueRequest(queueName: String)

object DeleteQueueRequest {

  implicit val format: Format[DeleteQueueRequest] = Json.format

  def apply(json: JsValue): Either[String, DeleteQueueRequest] =
    json.validate[DeleteQueueRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize DeleteQueueRequest $e")
    }
}
