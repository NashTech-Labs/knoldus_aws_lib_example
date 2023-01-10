package com.knoldus.aws.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.knoldus.aws.models.dynamodb.{ Question, QuestionUpdate }
import com.knoldus.aws.models.kinesis.{ CreateBankAccountEvent, UpdateBankAccountEvent }
import spray.json._

import java.util.UUID

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with UUIDProtocol {
  implicit val questionFormat: RootJsonFormat[Question] = jsonFormat4(Question.apply)
  implicit val questionUpdateFormat: RootJsonFormat[QuestionUpdate] = jsonFormat2(QuestionUpdate.apply)

  implicit val createBankAccountEventFormat: RootJsonFormat[CreateBankAccountEvent] = jsonFormat5(
    CreateBankAccountEvent.apply
  )

  implicit val updateBankAccountEventFormat: RootJsonFormat[UpdateBankAccountEvent] = jsonFormat3(
    UpdateBankAccountEvent.apply
  )
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
