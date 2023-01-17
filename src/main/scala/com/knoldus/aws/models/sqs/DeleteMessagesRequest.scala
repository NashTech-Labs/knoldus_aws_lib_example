package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class DeleteMessagesRequest(queueName: String, receiptHandles: Seq[String])

object DeleteMessagesRequest {

  implicit val format: Format[DeleteMessagesRequest] = Json.format

  def apply(json: JsValue): Either[String, DeleteMessagesRequest] =
    json.validate[DeleteMessagesRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize DeleteMessagesRequest $e")
    }
}
