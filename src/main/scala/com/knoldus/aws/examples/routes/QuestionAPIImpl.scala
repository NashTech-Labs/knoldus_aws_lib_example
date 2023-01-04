package com.knoldus.aws.examples.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.knoldus.aws.examples.models.Question
import com.knoldus.aws.examples.services.QuestionService
import com.knoldus.aws.examples.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

class QuestionAPIImpl(questionService: QuestionService) extends QuestionAPI with LazyLogging with JsonSupport {
  val routes: Route = submitQuestion ~ getQuestion ~ updateQuestion() ~ deleteQuestion()

  override def submitQuestion: Route =
    path("question") {
      pathEnd {
        (post & entity(as[Question])) { questionSubmissionRequest =>
          logger.info(s"Making request for question submission")
          val response = questionService.submitQuestion(questionSubmissionRequest)
          complete(response)
        }
      }
    }

  override def getQuestion: Route = ???

  override def updateQuestion(): Route = ???

  override def deleteQuestion(): Route = ???
}
