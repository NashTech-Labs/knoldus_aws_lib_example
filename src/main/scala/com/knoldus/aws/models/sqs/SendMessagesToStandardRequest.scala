package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class SendMessagesToStandardRequest(
  queueName: String,
  messageAttributes: Option[Map[String, String]],
  messageBodies: Seq[String],
  delaySeconds: Option[Int]
)

object SendMessagesToStandardRequest {

  implicit val format: Format[SendMessagesToStandardRequest] = Json.format

  def apply(json: JsValue): Either[String, SendMessagesToStandardRequest] =
    json.validate[SendMessagesToStandardRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize SendMessageToFifoRequest $e")
    }
}
