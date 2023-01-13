package com.knoldus.aws.services.kinesis.processor

import software.amazon.kinesis.processor.{ ShardRecordProcessor, ShardRecordProcessorFactory }

class BankAccountEventProcessorFactory(tableName: String) extends ShardRecordProcessorFactory {
  override def shardRecordProcessor(): ShardRecordProcessor = new BankAccountEventProcessor(tableName)
}
