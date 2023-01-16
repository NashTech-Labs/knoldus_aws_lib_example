package com.knoldus.aws.routes.dynamodb

import akka.http.scaladsl.server.Route

trait QuestionAPI {
  def submitQuestion: Route

  def getQuestion: Route

  def updateQuestion(): Route

  def deleteQuestion(): Route
}
