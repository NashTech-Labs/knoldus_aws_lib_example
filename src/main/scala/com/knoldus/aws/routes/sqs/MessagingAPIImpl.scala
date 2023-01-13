package com.knoldus.aws.routes.sqs

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import com.knoldus.aws.models.sqs._
import com.knoldus.aws.services.sqs.MessagingServiceImpl
import com.knoldus.aws.utils.Constants._
import com.knoldus.aws.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging
import spray.json.enrichAny

class MessagingAPIImpl(messagingServiceImpl: MessagingServiceImpl)
    extends MessagingAPI
    with JsonSupport
    with LazyLogging {

  val routes: Route =
    createQueue ~ listQueues ~ deleteQueue() ~ sendMsgToFIFOQueue ~ sendMsgToStandardQueue ~
        sendMultipleMsgToFIFOQueue ~ sendMultipleMsgToStandardQueue ~ receiveMessage ~
        deleteMessage() ~ deleteMultipleMessages()

  implicit val noSuchElementExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound, e.getMessage)
  }

  override def createQueue: Route =
    path("queue" / "create") {
      pathEnd {
        (post & entity(as[CreateQueueRequest])) { createQueueRequest =>
          logger.info("Making request for creating a new queue.")
          messagingServiceImpl.createNewQueue(createQueueRequest.queueName, createQueueRequest.queueType) match {
            case Left(ex) =>
              complete(
                HttpResponse(
                  StatusCodes.BadRequest,
                  entity = HttpEntity(ContentTypes.`application/json`, s"Exception ${ex.getMessage}")
                )
              )
            case Right(queue) =>
              complete(
                HttpResponse(
                  StatusCodes.OK,
                  entity = HttpEntity(ContentTypes.`application/json`, queue.toString)
                )
              )
          }
        }
      }
    }

  override def listQueues: Route =
    path("queue" / "allQueues") {
      pathEnd {
        get {
          handleExceptions(noSuchElementExceptionHandler) {
            logger.info(s"Making request for retrieving all the queues.")
            val queueSeq = messagingServiceImpl.listingQueueNames
            if (queueSeq.isEmpty)
              complete(
                HttpResponse(
                  StatusCodes.NoContent
                )
              )
            else
              complete(
                HttpResponse(
                  StatusCodes.OK,
                  entity = HttpEntity(ContentTypes.`application/json`, queueSeq.toJson.prettyPrint)
                )
              )
          }
        }
      }
    }

  override def deleteQueue(): Route =
    path("queue" / "delete") {
      pathEnd {
        (delete & entity(as[DeleteQueueRequest])) { deleteQueueRequest =>
          logger.info("Making request for deleting a queue.")
          messagingServiceImpl.searchQueueByName(deleteQueueRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.deletingQueue(queue) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(
                        ContentTypes.`application/json`,
                        s"The specified queue could not be deleted. : ${ex.getMessage}"
                      )
                    )
                  )
                case Right(_) =>
                  complete(
                    HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, QUEUE_DELETED))
                  )
              }
          }
        }
      }
    }

  override def sendMsgToFIFOQueue: Route =
    path("queue" / "fifo" / "sendMessage") {
      pathEnd {
        (post & entity(as[SendMessageToFifoRequest])) { sendMessageToFifoRequest =>
          logger.info("Making request for sending a message to fifo queue")
          messagingServiceImpl.searchQueueByName(sendMessageToFifoRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.sendMessageToQueue(
                queue,
                sendMessageToFifoRequest.messageBody,
                Some(sendMessageToFifoRequest.messageGroupId),
                sendMessageToFifoRequest.messageAttributes,
                sendMessageToFifoRequest.delaySeconds
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(ContentTypes.`application/json`, s"Exception ${ex.getMessage}")
                    )
                  )
                case Right(msg) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, msg)
                    )
                  )
              }

          }
        }
      }
    }

  override def sendMsgToStandardQueue: Route =
    path("queue" / "standard" / "sendMessage") {
      pathEnd {
        (post & entity(as[SendMessageToStandardRequest])) { sendMessageToStandardRequest =>
          logger.info("Making request for sending a message to standard queue.")
          messagingServiceImpl.searchQueueByName(sendMessageToStandardRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.sendMessageToQueue(
                queue,
                sendMessageToStandardRequest.messageBody,
                None,
                sendMessageToStandardRequest.messageAttributes,
                sendMessageToStandardRequest.delaySeconds
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(ContentTypes.`application/json`, s"Exception ${ex.getMessage}")
                    )
                  )
                case Right(msg) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, msg)
                    )
                  )
              }
          }
        }

      }
    }

  override def sendMultipleMsgToFIFOQueue: Route =
    path("queue" / "fifo" / "sendMultipleMessage") {
      pathEnd {
        (post & entity(as[SendMessagesToFifoRequest])) { sendMessagesToFifoRequest =>
          logger.info("Making request for sending multiple messages to fifo queue.")
          messagingServiceImpl.searchQueueByName(sendMessagesToFifoRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.sendMultipleMessagesToQueue(
                queue,
                sendMessagesToFifoRequest.messageBodies,
                Some(sendMessagesToFifoRequest.messageGroupId),
                sendMessagesToFifoRequest.messageAttributes,
                sendMessagesToFifoRequest.delaySeconds
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(ContentTypes.`application/json`, s"Exception ${ex.getMessage}")
                    )
                  )
                case Right(msg) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, msg)
                    )
                  )
              }
          }

        }
      }
    }

  override def sendMultipleMsgToStandardQueue: Route =
    path("queue" / "standard" / "sendMultipleMessages") {
      pathEnd {
        (post & entity(as[SendMessagesToStandardRequest])) { sendMessagesToStandardRequest =>
          logger.info("Making request for sending multiple messages to standard queue.")
          messagingServiceImpl.searchQueueByName(sendMessagesToStandardRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.sendMultipleMessagesToQueue(
                queue,
                sendMessagesToStandardRequest.messageBodies,
                None,
                sendMessagesToStandardRequest.messageAttributes,
                sendMessagesToStandardRequest.delaySeconds
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(ContentTypes.`application/json`, s"Exception ${ex.getMessage}")
                    )
                  )
                case Right(msg) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, msg)
                    )
                  )
              }
          }
        }
      }
    }

  override def receiveMessage: Route =
    path("queue" / "receiveMessage") {
      pathEnd {
        (get & entity(as[ReceiveMessageRequest])) { receiveMessageRequest =>
          logger.info("Making request for receiving messages")
          messagingServiceImpl.searchQueueByName(receiveMessageRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.receiveMessage(
                queue,
                receiveMessageRequest.maxNumberOfMessages.getOrElse(10),
                receiveMessageRequest.waitForSeconds.getOrElse(0)
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(
                        ContentTypes.`application/json`,
                        s"Cannot be received message for the specified queue : ${ex.getMessage}"
                      )
                    )
                  )
                case Right(messages) =>
                  val messageResponse = messages.map { message =>
                    MessageResponse(message.id, message.body, message.receiptHandle)
                  }
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, messageResponse.toJson.prettyPrint)
                    )
                  )
              }

          }
        }
      }
    }

  override def deleteMessage(): Route =
    path("queue" / "deleteMessage") {
      pathEnd {
        (delete & entity(as[DeleteMessageRequest])) { deleteMessageRequest =>
          logger.info("Making request for deleting messages from queue: " + deleteMessageRequest.queueName)
          messagingServiceImpl.searchQueueByName(deleteMessageRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.deleteMessageFromQueue(
                queue.url,
                deleteMessageRequest.receiptHandle
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(
                        ContentTypes.`application/json`,
                        s"Cannot be delete message for the specified queue : ${ex.getMessage}"
                      )
                    )
                  )
                case Right(_) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, MESSAGE_DELETED)
                    )
                  )
              }
          }
        }
      }
    }

  override def deleteMultipleMessages(): Route =
    path("queue" / "deleteMultipleMessages") {
      pathEnd {
        (delete & entity(as[DeleteMessagesRequest])) { deleteMessagesRequest =>
          logger.info("Making request for deleting multiple messages from queue: " + deleteMessagesRequest.queueName)
          messagingServiceImpl.searchQueueByName(deleteMessagesRequest.queueName) match {
            case None =>
              complete(
                HttpResponse(
                  StatusCodes.NotFound,
                  entity = HttpEntity(ContentTypes.`application/json`, QUEUE_NOT_FOUND)
                )
              )
            case Some(queue) =>
              messagingServiceImpl.deleteMultipleMessagesFromQueue(
                queue.url,
                deleteMessagesRequest.receiptHandles
              ) match {
                case Left(ex) =>
                  complete(
                    HttpResponse(
                      StatusCodes.InternalServerError,
                      entity = HttpEntity(
                        ContentTypes.`application/json`,
                        s"Cannot be delete multiple messages for the specified queue : ${ex.getMessage}"
                      )
                    )
                  )
                case Right(_) =>
                  complete(
                    HttpResponse(
                      StatusCodes.OK,
                      entity = HttpEntity(ContentTypes.`application/json`, MESSAGES_DELETED)
                    )
                  )
              }
          }
        }
      }
    }
}
