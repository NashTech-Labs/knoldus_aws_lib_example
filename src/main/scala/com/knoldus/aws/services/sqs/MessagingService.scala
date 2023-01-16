package com.knoldus.aws.services.sqs

import com.knoldus.sqs.models.QueueType.QueueType
import com.knoldus.sqs.models.{ Message, Queue }

trait MessagingService {

  def createNewQueue(queueName: String, queueType: QueueType): Either[Throwable, Queue]

  def listingQueues: Seq[Queue]

  def listingQueueNames: Seq[String]

  def searchQueueByName(queueName: String): Option[Queue]

  def deletingQueue(queue: Queue): Either[Throwable, String]

  def sendMessageToQueue(
    queue: Queue,
    messageBody: String,
    messageGroupId: Option[String] = None,
    messageAttributes: Option[Map[String, String]] = None,
    delaySeconds: Option[Int] = None
  ): Either[Throwable, String]

  def sendMultipleMessagesToQueue(
    queue: Queue,
    messageBodies: Seq[String],
    messageGroupId: Option[String] = None,
    messageAttributes: Option[Map[String, String]] = None,
    delaySeconds: Option[Int] = None
  ): Either[Throwable, String]

  def receiveMessage(queue: Queue, maxNumberOfMessages: Int, waitForSeconds: Int): Either[Throwable, Seq[Message]]

  def deleteMessageFromQueue(queue: Queue, receiptHandle: String): Either[Throwable, String]

  def deleteMultipleMessagesFromQueue(queue: Queue, receiptHandle: Seq[String]): Either[Throwable, String]

}
