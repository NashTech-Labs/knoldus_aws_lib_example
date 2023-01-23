package com.knoldus.aws.routes.dynamodb

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import com.knoldus.aws.models.dynamodb.{ Question, QuestionUpdate }
import com.knoldus.aws.services.dynamodb.QuestionService
import com.knoldus.aws.utils.{ Constants, JsonSupport }
import com.typesafe.scalalogging.LazyLogging

class QuestionAPIImpl(questionService: QuestionService) extends QuestionAPI with LazyLogging with JsonSupport {
  val routes: Route = submitQuestion ~ getQuestion ~ updateQuestion() ~ deleteQuestion() ~ getQuestions

  val noSuchElementExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound, e.getMessage)
  }

  override def submitQuestion: Route =
    path("questions" / "submit") {
      pathEnd {
        (post & entity(as[Question])) { questionSubmissionRequest =>
          logger.info("Making request for question submission")
          val response = questionService.submitQuestion(questionSubmissionRequest)
          complete(response)
        }
      }
    }

  override def getQuestion: Route =
    path("questions" / Segment / Segment) { (questionId, category) =>
      pathEnd {
        get {
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for getting the question with id: $questionId and category: $category")
            val response = questionService.getQuestion(questionId, category)
            complete(response)
          }
        }
      }
    }

  override def getQuestions: Route =
    path("questions" / IntNumber) { latestQuestionCount =>
      pathEnd {
        get {
          logger.info(s"Making request for getting the latest questions")
          val response = questionService.getQuestions(latestQuestionCount)
          complete(response)
        }
      }
    }

  override def updateQuestion(): Route =
    path("questions" / "update" / Segment / Segment) { (questionId, category) =>
      pathEnd {
        (put & entity(as[QuestionUpdate])) { questionUpdateRequest =>
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request to update a question")
            val response = questionService.updateQuestion(questionId, category, questionUpdateRequest)
            complete(response)
          }
        }
      }
    }

  override def deleteQuestion(): Route =
    path("questions" / Segment / Segment) { (questionId, category) =>
      pathEnd {
        delete {
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for deleting a question")
            val responseFuture = questionService.deleteQuestion(questionId, category)
            onSuccess(responseFuture) { questionDeleted =>
              if (questionDeleted)
                complete(Constants.questionDeletedResponse)
              else
                complete(StatusCodes.InternalServerError, Constants.questionNotDeletedResponse)
            }
          }
        }
      }
    }
}
