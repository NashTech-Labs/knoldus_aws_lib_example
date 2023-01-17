package com.knoldus.aws.services.kinesis.processor

import com.typesafe.scalalogging.LazyLogging
import software.amazon.kinesis.lifecycle.events._
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.retrieval.KinesisClientRecord

import java.nio.charset.StandardCharsets

class BankAccountEventProcessor(tableName: String) extends ShardRecordProcessor with LazyLogging {

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

  private def processRecord(record: KinesisClientRecord): Unit =
    // ToDo: Add events processing logic + Update the DB
    logger.info(
      s"Processing record pk: ${record.partitionKey()} -- Data: ${StandardCharsets.UTF_8.decode(record.data).toString}"
    )

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
