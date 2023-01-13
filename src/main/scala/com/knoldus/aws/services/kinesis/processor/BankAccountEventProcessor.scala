package com.knoldus.aws.services.kinesis.processor

import software.amazon.kinesis.lifecycle.events.{
  InitializationInput,
  LeaseLostInput,
  ProcessRecordsInput,
  ShardEndedInput,
  ShutdownRequestedInput
}
import software.amazon.kinesis.processor.ShardRecordProcessor

class BankAccountEventProcessor(tableName: String) extends ShardRecordProcessor {
  override def initialize(initializationInput: InitializationInput): Unit = ???

  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit = ???

  override def leaseLost(leaseLostInput: LeaseLostInput): Unit = ???

  override def shardEnded(shardEndedInput: ShardEndedInput): Unit = ???

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit = ???
}
