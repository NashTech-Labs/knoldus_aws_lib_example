package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class MessageResponse(messageId: String, messageBody: String, receiptHandle: String)

object MessageResponse {

  implicit val format: Format[MessageResponse] = Json.format

  def apply(json: JsValue): Either[String, MessageResponse] =
    json.validate[MessageResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize MessageResponse $e")
    }
}
