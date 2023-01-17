package com.knoldus.aws.models.sqs

import com.knoldus.sqs.models.QueueType
import com.knoldus.sqs.models.QueueType.QueueType
import play.api.libs.json._

case class CreateQueueResponse(queueName: String, queueType: QueueType, queueUrl: String)

object CreateQueueResponse {

  implicit val QueueTypeReads: Reads[QueueType.Value] = Reads.enumNameReads(QueueType)
  implicit val QueueTypeWrites: Writes[QueueType.Value] = Writes.enumNameWrites

  implicit val format: Format[CreateQueueResponse] = Json.format

  def apply(json: JsValue): Either[String, CreateQueueResponse] =
    json.validate[CreateQueueResponse] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize CreateQueueResponse $e")
    }
}
