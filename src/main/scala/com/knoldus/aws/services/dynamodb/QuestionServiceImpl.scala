package com.knoldus.aws.services.dynamodb

import com.knoldus.aws.models.dynamodb.{ Question, QuestionTable, QuestionUpdate }
import com.knoldus.aws.utils.Constants
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

class QuestionServiceImpl(questionTable: QuestionTable) extends QuestionService with LazyLogging {

  override def submitQuestion(question: Question): Future[String] =
    Future {
      questionTable.put(question.record) match {
        case Right(id) =>
          logger.info("Question submitted successfully")
          Constants.submitQuestionResponse(id)
        case Left(message) =>
          logger.error(s"Failed to submitted the question: $message")
          Constants.questionNotSubmittedResponse
      }
    }

  override def getQuestion(id: String, category: String): Future[Option[Question]] =
    Future {
      logger.info(s"Getting the question with id: $id and category: $category")
      questionTable.retrieve(category, id) match {
        case Some(record) =>
          logger.info("Question found")
          Question(Json.parse(record.json)) match {
            case Left(message) =>
              logger.error(message)
              None
            case Right(question) => Some(question)
          }
        case None =>
          logger.info("Question not found")
          throw new NoSuchElementException(Constants.noQuestionFoundResponse)
      }
    }

  override def updateQuestion(id: String, category: String, questionUpdate: QuestionUpdate): Future[String] =
    Future {
      def updateQuestionEntity(): Question = {
        val updatedTitle = questionUpdate.title
        val updatedDescription = questionUpdate.description
        Question(id, updatedTitle, category, updatedDescription)
      }
      logger.info(s"Updating the question with id: $id and category: $category")
      questionTable.update(category, id, updateQuestionEntity().record) match {
        case Right(_) =>
          Constants.updateQuestionResponse
        case Left(message) =>
          logger.error(s"Failed to update the question, reason: $message")
          throw new NoSuchElementException(Constants.noQuestionFoundResponse)
      }
    }

  override def deleteQuestion(id: String, category: String): Future[Boolean] =
    Future {
      logger.info(s"Deleting the question with id: $id and category: $category")
      Try(questionTable.delete(category, id)) match {
        case Failure(exception) =>
          logger.error(s"Failed to delete the question, reason: ${exception.getMessage}")
          false
        case Success(_) =>
          logger.info(s"Question Deleted Successfully")
          true
      }
    }
}
