package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class DeleteMessageRequest(queueName: String, receiptHandle: String)

object DeleteMessageRequest {

  implicit val format: Format[DeleteMessageRequest] = Json.format

  def apply(json: JsValue): Either[String, DeleteMessageRequest] =
    json.validate[DeleteMessageRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize DeleteMessageRequest $e")
    }
}
