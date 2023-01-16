package com.knoldus.aws.services.sqs

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.sqs.AmazonSQS
import com.knoldus.aws.utils.Constants._
import com.knoldus.common.aws.CredentialsLookup
import com.knoldus.sqs.models.QueueType.QueueType
import com.knoldus.sqs.models.{ Message, Queue, SQSConfig }
import com.knoldus.sqs.services.SQSService
import com.knoldus.sqs.services.SQSService.buildAmazonSQSClient
import com.typesafe.scalalogging.LazyLogging

import scala.util.{ Failure, Success, Try }

class MessagingServiceImpl(sqsConfig: SQSConfig) extends MessagingService with LazyLogging {

  implicit val sqsService: SQSService = new SQSService {

    override val config: SQSConfig = sqsConfig

    override val amazonSQSClient: AmazonSQS = {
      val credentials: AWSCredentialsProvider =
        CredentialsLookup.getCredentialsProvider(config.awsConfig.awsAccessKey, config.awsConfig.awsSecretKey)
      buildAmazonSQSClient(config, credentials)
    }
  }

  override def createNewQueue(queueName: String, queueType: QueueType): Either[Throwable, Queue] =
    sqsService.createQueue(queueName, queueType) match {
      case Left(exception) =>
        logger.info(s"Failed to create new ${queueType.toString} queue $queueName: ${exception.getMessage}")
        Left(exception)
      case Right(queue) =>
        logger.info(s"Successfully created new ${queueType.toString} queue: ${queue.queueName}")
        Right(queue)
    }

  override def deletingQueue(queue: Queue): Either[Throwable, String] =
    Try(sqsService.deleteQueue(queue)) match {
      case Failure(exception) =>
        logger.info(s"Failed to delete queue ${queue.queueName}: ${exception.getMessage}")
        Left(exception)
      case Success(_) =>
        logger.info(s"Successfully deleted queue: ${queue.queueName}")
        Right(QUEUE_DELETED)
    }

  override def listingQueues: Seq[Queue] = {
    logger.info(s"Fetching list of queues.")
    sqsService.listQueues
  }

  override def listingQueueNames: Seq[String] = {
    logger.info(s"Fetching list of queues.")
    sqsService.listQueuesByName
  }

  override def searchQueueByName(queueName: String): Option[Queue] = {
    logger.info(s"Searching $queueName in the list of queues.")
    sqsService.findQueueByName(queueName)
  }

  override def sendMessageToQueue(
    queue: Queue,
    messageBody: String,
    messageGroupId: Option[String],
    messageAttributes: Option[Map[String, String]],
    delaySeconds: Option[Int]
  ): Either[Throwable, String] =
    Try(
      sqsService.sendMessage(
        queue: Queue,
        messageBody: String,
        messageGroupId: Option[String],
        messageAttributes: Option[Map[String, String]],
        delaySeconds: Option[Int]
      )
    ) match {
      case Failure(exception) =>
        logger.info(
          s"Failed to send message to ${queue.queueType.toString} queue ${queue.queueName}: ${exception.getMessage}"
        )
        Left(exception)
      case Success(_) =>
        logger.info(s"Successfully sent message to ${queue.queueType.toString} queue: ${queue.queueName}")
        Right(MESSAGE_SENT)
    }

  override def sendMultipleMessagesToQueue(
    queue: Queue,
    messageBodies: Seq[String],
    messageGroupId: Option[String],
    messageAttributes: Option[Map[String, String]],
    delaySeconds: Option[Int]
  ): Either[Throwable, String] =
    Try(
      sqsService.sendMessages(
        queue: Queue,
        messageBodies: Seq[String],
        messageGroupId: Option[String],
        messageAttributes: Option[Map[String, String]],
        delaySeconds: Option[Int]
      )
    ) match {
      case Failure(exception) =>
        logger.info(
          s"Failed to send multiple messages to ${queue.queueType.toString} queue ${queue.queueName}: ${exception.getMessage}"
        )
        Left(exception)
      case Success(_) =>
        logger.info(s"Successfully sent multiple messages to ${queue.queueType.toString} queue: ${queue.queueName}")
        Right(MESSAGES_SENT)
    }

  override def receiveMessage(
    queue: Queue,
    maxNumberOfMessages: Int = TEN,
    waitForSeconds: Int = ZERO
  ): Either[Throwable, Seq[Message]] =
    Try(sqsService.receiveMessages(queue, maxNumberOfMessages, waitForSeconds)) match {
      case Failure(exception) =>
        logger.info(s"Failed to receive messages from queue ${queue.queueName}: ${exception.getMessage}")
        Left(exception)
      case Success(value) =>
        logger.info(s"Successfully received messages from queue: ${queue.queueName}")
        Right(value)
    }

  override def deleteMessageFromQueue(queue: Queue, receiptHandle: String): Either[Throwable, String] =
    Try(sqsService.deleteMessage(queue.url, receiptHandle)) match {
      case Failure(exception) =>
        logger.info(s"Failed to delete message from queue ${queue.queueName}: ${exception.getMessage}")
        Left(exception)
      case Success(_) =>
        logger.info(s"Successfully deleted message from queue ${queue.queueName}.")
        Right(MESSAGE_DELETED)
    }

  override def deleteMultipleMessagesFromQueue(queue: Queue, receiptHandle: Seq[String]): Either[Throwable, String] =
    Try(sqsService.deleteMessages(queue.url, receiptHandle)) match {
      case Failure(exception) =>
        logger.info(s"Failed to delete multiple messages from queue ${queue.queueName}: ${exception.getMessage}")
        Left(exception)
      case Success(_) =>
        logger.info(s"Successfully deleted multiple messages from queue ${queue.queueName}.")
        Right(MESSAGES_DELETED)
    }
}
