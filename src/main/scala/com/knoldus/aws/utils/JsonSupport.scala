package com.knoldus.aws.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.knoldus.aws.models.dynamodb.{ Question, QuestionUpdate }
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val questionFormat: RootJsonFormat[Question] = jsonFormat4(Question.apply)
  implicit val questionUpdateFormat: RootJsonFormat[QuestionUpdate] = jsonFormat2(QuestionUpdate.apply)
}
