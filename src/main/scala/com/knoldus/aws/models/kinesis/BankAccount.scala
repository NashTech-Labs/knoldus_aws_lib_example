package com.knoldus.aws.models.kinesis

import com.knoldus.dynamodb.models.{ DynamoItem, DynamoRecord }
import play.api.libs.json.{ Format, JsSuccess, JsValue, Json }

import java.time.Instant
import java.util.UUID

case class BankAccount(
  accountNumber: UUID,
  accountOwner: String,
  accountType: String,
  securityCode: String,
  balance: Double
) extends DynamoItem {
  override def partitionKey: String = this.productPrefix

  override def sortKey: String = accountNumber.toString

  override def json: String = Json.stringify(Json.toJson(this))

  override def timestamp: Long = Instant.now().toEpochMilli
}

object BankAccount {
  implicit val format: Format[BankAccount] = Json.format

  def apply(record: DynamoRecord): Option[BankAccount] =
    BankAccount(Json.parse(record.json)).toOption

  def apply(json: JsValue): Either[String, BankAccount] =
    json.validate[BankAccount] match {
      case JsSuccess(user, _) => Right(user)
      case e => Left(s"Failed to deserialize Bank Account, $e")
    }
}
