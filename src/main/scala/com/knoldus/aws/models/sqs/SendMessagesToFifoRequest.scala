package com.knoldus.aws.models.sqs

import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

case class SendMessagesToFifoRequest(
  queueName: String,
  messageBodies: Seq[String],
  messageGroupId: String,
  messageAttributes: Option[Map[String, String]],
  delaySeconds: Option[Int]
)

object SendMessagesToFifoRequest {

  implicit val format: Format[SendMessagesToFifoRequest] = Json.format

  def apply(json: JsValue): Either[String, SendMessagesToFifoRequest] =
    json.validate[SendMessagesToFifoRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize SendMessageToFifoRequest $e")
    }
}
