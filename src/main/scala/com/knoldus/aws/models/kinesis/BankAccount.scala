package com.knoldus.aws.models.kinesis

import com.knoldus.dynamodb.models.DynamoItem
import play.api.libs.json.{Format, JsSuccess, JsValue, Json}

import java.time.Instant
import java.util.UUID

case class BankAccount(
  accountNumber: UUID,
  accountOwner: String,
  accountType: String,
  securityCode: String,
  balance: Double
) extends DynamoItem {
  override def partitionKey: String = accountType

  override def sortKey: String = accountOwner

  override def json: String = Json.stringify(Json.toJson(this))

  override def timestamp: Long = Instant.now().toEpochMilli
}

object BankAccount {
  implicit val format: Format[BankAccount] = Json.format

  def apply(json: JsValue): Either[String, BankAccount] =
    json.validate[BankAccount] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize Question $e")
    }
}
