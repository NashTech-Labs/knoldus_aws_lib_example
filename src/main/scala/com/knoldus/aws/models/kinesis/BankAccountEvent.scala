package com.knoldus.aws.models.kinesis

import java.util.UUID
import play.api.libs.json._

sealed trait BankAccountEvent {
  def eventType: String
  def accountNumber: UUID
  def toJsonString: String
}

object BankAccountEvent {
  implicit val createBankAccountEventReads: Reads[CreateBankAccountEvent] = Json.reads[CreateBankAccountEvent]
  implicit val updateBankAccountEventReads: Reads[UpdateBankAccountEvent] = Json.reads[UpdateBankAccountEvent]

  implicit val bankAccountEventReads: Reads[BankAccountEvent] = (json: JsValue) => {
    (json \ "eventType").as[String] match {
      case "create" => json.validate[CreateBankAccountEvent](createBankAccountEventReads)
      case "update" => json.validate[UpdateBankAccountEvent](updateBankAccountEventReads)
    }
  }
}

case class CreateBankAccountEvent(
  eventType: String,
  accountNumber: UUID,
  accountOwner: String,
  accountType: String,
  securityCode: String,
  balance: Double
) extends BankAccountEvent {

  override def toJsonString: String = Json.stringify(Json.toJson(this))
}

object CreateBankAccountEvent {

  implicit val format: Format[CreateBankAccountEvent] = Json.format
}

case class UpdateBankAccountEvent(eventType: String, accountNumber: UUID, updateType: String, amount: Double)
    extends BankAccountEvent {

  override def toJsonString: String = Json.stringify(Json.toJson(this))
}

object UpdateBankAccountEvent {
  implicit val format: Format[UpdateBankAccountEvent] = Json.format
}
