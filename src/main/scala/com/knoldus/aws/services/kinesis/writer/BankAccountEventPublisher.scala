package com.knoldus.aws.services.kinesis.writer

import com.knoldus.aws.models.kinesis.BankAccountEvent
import com.knoldus.kinesis.model.KinesisUserRecord
import com.knoldus.kinesis.producer.KinesisDataProducer
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BankAccountEventPublisher(val config: Config) extends LazyLogging {

  private val kinesisDataProducer = new KinesisDataProducer(config)

  def publishBankAccountEvent(stream: String, event: BankAccountEvent): Future[Boolean] = {
    val kinesisUserRecord = KinesisUserRecord(stream, event.accountNumber.toString, event.toJsonString)
    logger.info(s"Publishing the event to $stream data stream")
    kinesisDataProducer.putRecord(kinesisUserRecord).map { result =>
      logger.info(
        s"Sequence Number: ${result.getSequenceNumber}, Shard Id: ${result.getShardId}, Number of Attempts: ${result.getAttempts.size()}"
      )
      result.isSuccessful
    }
  }

}
