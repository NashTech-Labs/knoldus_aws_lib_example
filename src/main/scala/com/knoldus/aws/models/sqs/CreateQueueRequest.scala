package com.knoldus.aws.models.sqs

import com.knoldus.sqs.models.QueueType
import com.knoldus.sqs.models.QueueType.QueueType
import play.api.libs.json._

case class CreateQueueRequest(queueName: String, queueType: QueueType)

object CreateQueueRequest {

  implicit val QueueTypeReads: Reads[QueueType.Value] = Reads.enumNameReads(QueueType)
  implicit val QueueTypeWrites: Writes[QueueType.Value] = Writes.enumNameWrites

  implicit val format: Format[CreateQueueRequest] = Json.format

  def apply(json: JsValue): Either[String, CreateQueueRequest] =
    json.validate[CreateQueueRequest] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize CreateQueueRequest $e")
    }
}
