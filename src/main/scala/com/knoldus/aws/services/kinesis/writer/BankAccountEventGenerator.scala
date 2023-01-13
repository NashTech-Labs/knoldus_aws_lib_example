package com.knoldus.aws.services.kinesis.writer

import com.knoldus.aws.models.kinesis.{
  BankAccountCreationEventRequest,
  BankAccountEvent,
  CreateBankAccountEvent,
  UpdateBankAccountEvent
}
import com.typesafe.scalalogging.LazyLogging

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BankAccountEventGenerator(bankAccountEventPublisher: BankAccountEventPublisher) extends LazyLogging {
  val stream: String = bankAccountEventPublisher.config.getString("data-stream-name")

  def createBankAccountEvent(bankAccountDetails: BankAccountCreationEventRequest): Future[BankAccountEvent] = {
    val newAccountNumber = UUID.randomUUID()
    val bankAccountEvent = CreateBankAccountEvent(
      newAccountNumber,
      bankAccountDetails.accountOwner,
      bankAccountDetails.accountType,
      bankAccountDetails.securityCode,
      bankAccountDetails.initialBalance
    )
    logger.info(s"Generated an event for creating a new bank account")
    val publishEventResultFuture = bankAccountEventPublisher.publishBankAccountEvent(stream, bankAccountEvent)
    checkAndGetPublishedEvent(publishEventResultFuture, bankAccountEvent)
  }

  def creditBankAccountEvent(accountNumber: UUID, amountToCredit: Double): Future[BankAccountEvent] = {
    val bankAccountEvent = UpdateBankAccountEvent(accountNumber, "Credit", amountToCredit)
    val publishEventResultFuture = bankAccountEventPublisher.publishBankAccountEvent(stream, bankAccountEvent)
    logger.info(s"Generated an event to credit a bank account")
    checkAndGetPublishedEvent(publishEventResultFuture, bankAccountEvent)
  }

  def debitBankAccountEvent(accountNumber: UUID, amountToDebit: Double): Future[BankAccountEvent] = {
    val bankAccountEvent = UpdateBankAccountEvent(accountNumber, "Debit", amountToDebit)
    val publishEventResultFuture = bankAccountEventPublisher.publishBankAccountEvent(stream, bankAccountEvent)
    logger.info(s"Generated an event to debit a bank account")
    checkAndGetPublishedEvent(publishEventResultFuture, bankAccountEvent)
  }

  private def checkAndGetPublishedEvent(
    publishEventResult: Future[Boolean],
    event: BankAccountEvent
  ): Future[BankAccountEvent] =
    publishEventResult.map { isEventPublished =>
      if (isEventPublished) {
        logger.info(s"Event published successfully to the $stream data stream")
        event
      } else {
        logger.error(s"Failure occurs publishing the event to $stream data stream")
        throw new Exception("Event not published")
      }
    }

}
