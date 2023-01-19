package com.knoldus.aws.services.kinesis.processor

import com.knoldus.aws.models.kinesis.BankAccountTable
import software.amazon.kinesis.processor.{ ShardRecordProcessor, ShardRecordProcessorFactory }

class BankAccountEventProcessorFactory(bankAccountTable: BankAccountTable) extends ShardRecordProcessorFactory {
  override def shardRecordProcessor(): ShardRecordProcessor = new BankAccountEventProcessor(bankAccountTable)
}
