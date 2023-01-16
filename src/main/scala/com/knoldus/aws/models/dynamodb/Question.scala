package com.knoldus.aws.models.dynamodb

import com.knoldus.dynamodb.models.DynamoItem
import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

import java.time.Instant

case class Question(id: String, title: String, category: String, description: String) extends DynamoItem {
  override def partitionKey: String = category

  override def sortKey: String = id

  override def json: String = Json.stringify(Json.toJson(this))

  override def timestamp: Long = Instant.now().toEpochMilli
}

object Question {
  implicit val format: Format[Question] = Json.format

  def apply(json: JsValue): Either[String, Question] =
    json.validate[Question] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize Question $e")
    }
}
