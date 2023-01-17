package com.knoldus.aws.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.knoldus.aws.models.dynamodb.{ Question, QuestionUpdate }
import com.knoldus.aws.models.kinesis.{
  BankAccountCreationEventRequest,
  BankAccountEvent,
  CreateBankAccountEvent,
  UpdateBankAccountEvent,
  UpdateBankAccountEventRequest
}
import spray.json._

import java.util.UUID

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with UUIDProtocol {
  implicit val questionFormat: RootJsonFormat[Question] = jsonFormat4(Question.apply)
  implicit val questionUpdateFormat: RootJsonFormat[QuestionUpdate] = jsonFormat2(QuestionUpdate.apply)

  implicit val bankAccountCreationEventRequestFormat: RootJsonFormat[BankAccountCreationEventRequest] = jsonFormat4(
    BankAccountCreationEventRequest.apply
  )

  implicit val updateBankAccountEventRequestFormat: RootJsonFormat[UpdateBankAccountEventRequest] = jsonFormat2(
    UpdateBankAccountEventRequest.apply
  )

  implicit val createBankAccountEventFormat: RootJsonFormat[CreateBankAccountEvent] = jsonFormat6(
    CreateBankAccountEvent.apply
  )

  implicit val updateBankAccountEventFormat: RootJsonFormat[UpdateBankAccountEvent] = jsonFormat4(
    UpdateBankAccountEvent.apply
  )

  implicit val bankAccountEventFormat: RootJsonFormat[BankAccountEvent] = new RootJsonFormat[BankAccountEvent] {

    def write(obj: BankAccountEvent): JsValue =
      JsObject((obj match {
        case c: CreateBankAccountEvent => c.toJson
        case d: UpdateBankAccountEvent => d.toJson
      }).asJsObject.fields)

    def read(json: JsValue): BankAccountEvent = ???
  }
}

trait UUIDProtocol {

  implicit object UUIDFormat extends RootJsonFormat[UUID] {

    def write(uuid: UUID): JsValue = JsString(uuid.toString)

    def read(value: JsValue): UUID =
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ => throw DeserializationException("Expected hexadecimal UUID string")
      }
  }
}
