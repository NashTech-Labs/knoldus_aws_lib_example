package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class SendMessageToStandardRequest(
  queueName: String,
  messageBody: String,
  messageAttributes: Option[Map[String, String]],
  delaySeconds: Option[Int]
)

object SendMessageToStandardRequest {

  implicit val format: Format[SendMessageToStandardRequest] = Json.format

  def apply(json: JsValue): Either[String, SendMessageToStandardRequest] =
    json.validate[SendMessageToStandardRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize SendMessageToFifoRequest $e")
    }
}
