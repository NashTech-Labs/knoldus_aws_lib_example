package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class SendMessageToFifoRequest(
  queueName: String,
  messageBody: String,
  messageGroupId: String,
  messageAttributes: Option[Map[String, String]],
  delaySeconds: Option[Int]
)

object SendMessageToFifoRequest {

  implicit val format: Format[SendMessageToFifoRequest] = Json.format

  def apply(json: JsValue): Either[String, SendMessageToFifoRequest] =
    json.validate[SendMessageToFifoRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize SendMessageToFifoRequest $e")
    }
}
