package com.knoldus.aws.services.kinesis.processor

import com.knoldus.aws.models.kinesis.{
  BankAccount,
  BankAccountEvent,
  BankAccountTable,
  CreateBankAccountEvent,
  UpdateBankAccountEvent
}
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
import software.amazon.kinesis.lifecycle.events._
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.retrieval.KinesisClientRecord

import java.nio.charset.StandardCharsets

class BankAccountEventProcessor(bankAccountTable: BankAccountTable) extends ShardRecordProcessor with LazyLogging {

  override def initialize(initializationInput: InitializationInput): Unit = {
    logger.info(s"Initializing record processor for shard: ${initializationInput.shardId}")
    logger.info(s"Initializing @ Sequence: ${initializationInput.extendedSequenceNumber.toString}")
  }

  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit =
    try {
      logger.info("Processing " + processRecordsInput.records.size + " record(s)")
      processRecordsInput.records.forEach((r: KinesisClientRecord) => processRecord(r))
    } catch {
      case _: Throwable =>
        logger.error("Caught throwable while processing records. Aborting.")
        Runtime.getRuntime.halt(1)
    }

  private def processRecord(record: KinesisClientRecord): Unit = {
    val eventString = StandardCharsets.UTF_8.decode(record.data).toString
    logger.info(
      s"Processing record pk: ${record.partitionKey()} -- Data: $eventString"
    )
    val eventJson = Json.parse(eventString)
    val event = eventJson.as[BankAccountEvent]

    event match {
      case CreateBankAccountEvent(_, accountNumber, accountOwner, accountType, securityCode, balance) =>
        val newBankAccount = BankAccount(accountNumber, accountOwner, accountType, securityCode, balance)
        logger.info(s"Creating a new $accountType bank account for $accountOwner")
        bankAccountTable.put(newBankAccount.record) match {
          case Right(_) =>
            logger.info("Bank account created successfully")
          case Left(errorMsg) =>
            logger.error(s"Failed to create bank account $accountNumber, reason: $errorMsg")
        }
      case UpdateBankAccountEvent(_, accountNumber, updateType, amount) =>
        updateType match {
          case "credit" =>
            logger.info(s"Getting bank account $accountNumber")
            bankAccountTable.retrieve("BankAccount", accountNumber.toString) match {
              case Some(bankAccountRecord) =>
                logger.info(s"Crediting bank account $accountNumber with amount $amount")
                BankAccount(Json.parse(bankAccountRecord.json)) match {
                  case Left(errorMsg) => logger.error(s"Bank account not credited, reason: $errorMsg")
                  case Right(bankAccount) =>
                    val updatedAccountBalance = bankAccount.balance + amount
                    val updatedBankAccountRecord = bankAccount.copy(balance = updatedAccountBalance).record
                    bankAccountTable.update("BankAccount", accountNumber.toString, updatedBankAccountRecord) match {
                      case Left(_) => logger.error(s"Failed to credit bank account")
                      case Right(_) =>
                        logger.info(s"Bank account $accountNumber successfully credited with amount $amount")
                    }
                }
              case None => logger.error(s"Bank account $accountNumber not exist")
            }
          case "debit" =>
            logger.info(s"Getting bank account $accountNumber")
            bankAccountTable.retrieve("BankAccount", accountNumber.toString) match {
              case Some(bankAccountRecord) =>
                logger.info(s"Debiting bank account $accountNumber with amount $amount")
                BankAccount(Json.parse(bankAccountRecord.json)) match {
                  case Left(errorMsg) => logger.error(s"Bank account not debited, reason: $errorMsg")
                  case Right(bankAccount) =>
                    if (bankAccount.balance >= amount) {
                      val updatedAccountBalance = bankAccount.balance - amount
                      val updatedBankAccountRecord = bankAccount.copy(balance = updatedAccountBalance).record
                      bankAccountTable.update("BankAccount", accountNumber.toString, updatedBankAccountRecord) match {
                        case Left(_) => logger.error(s"Failed to debit bank account")
                        case Right(_) =>
                          logger.info(s"Bank account $accountNumber successfully debited with amount $amount")
                      }
                    } else logger.error(s"Bank account $accountNumber can not be debited due to insufficient balance")
                }
              case None => logger.error(s"Bank account $accountNumber not exist")
            }
        }
    }

  }

  override def leaseLost(leaseLostInput: LeaseLostInput): Unit =
    logger.info("Lost lease, so terminating.")

  override def shardEnded(shardEndedInput: ShardEndedInput): Unit =
    try {
      // Important to checkpoint after reaching end of shard, so to start processing data from child shards.
      logger.info("Reached shard end checkpointing.")
      shardEndedInput.checkpointer.checkpoint()
    } catch {
      case e: Throwable =>
        logger.error("Exception while checkpointing at shard end. Giving up.", e)
    }

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit =
    try {
      logger.info("Scheduler is shutting down, checkpointing.")
      shutdownRequestedInput.checkpointer().checkpoint()
    } catch {
      case e: Throwable =>
        logger.error("Exception while checkpointing at requested shutdown. Giving up.", e)
    }
}
