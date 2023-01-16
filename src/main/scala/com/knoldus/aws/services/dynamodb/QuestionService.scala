package com.knoldus.aws.services.dynamodb

import com.knoldus.aws.models.dynamodb.{ Question, QuestionUpdate }

import scala.concurrent.Future

trait QuestionService {
  def submitQuestion(question: Question): Future[String]

  def getQuestion(id: String, category: String): Future[Option[Question]]

  def updateQuestion(id: String, category: String, update: QuestionUpdate): Future[String]

  def deleteQuestion(id: String, category: String): Future[Boolean]
}
