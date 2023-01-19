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
            logger.info(s"Crediting bank account $accountNumber with amount $amount")

          // ToDO - update the credited amount in bank account db record
          case "debit" =>
            logger.info(s"Debiting bank account $accountNumber with amount $amount")

          // ToDO - update the debited amount in bank account db record
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
