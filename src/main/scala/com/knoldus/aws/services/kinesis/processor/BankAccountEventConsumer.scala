package com.knoldus.aws.services.kinesis.processor

import com.knoldus.aws.models.kinesis.BankAccountTable
import com.knoldus.aws.utils.Constants
import com.knoldus.aws.utils.Constants.TWENTY
import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.{ ConfigsBuilder, KinesisClientUtil }
import software.amazon.kinesis.coordinator.Scheduler

import java.io.{ BufferedReader, IOException, InputStreamReader }
import java.util.UUID
import java.util.concurrent.{ ExecutionException, TimeUnit, TimeoutException }

class BankAccountEventConsumer(
  streamName: String,
  applicationName: String,
  region: Region,
  bankAccountTable: BankAccountTable
) extends LazyLogging {

  private val kinesisClient: KinesisAsyncClient =
    KinesisClientUtil.createKinesisAsyncClient(KinesisAsyncClient.builder.region(region))

  def run(): Unit = {

    val dynamoClient = DynamoDbAsyncClient.builder.region(region).build
    val cloudWatchClient = CloudWatchAsyncClient.builder.region(region).build
    val configsBuilder = new ConfigsBuilder(
      streamName,
      applicationName,
      kinesisClient,
      dynamoClient,
      cloudWatchClient,
      UUID.randomUUID.toString,
      new BankAccountEventProcessorFactory(bankAccountTable)
    )

    val scheduler = new Scheduler(
      configsBuilder.checkpointConfig,
      configsBuilder.coordinatorConfig,
      configsBuilder.leaseManagementConfig,
      configsBuilder.lifecycleConfig,
      configsBuilder.metricsConfig,
      configsBuilder.processorConfig,
      configsBuilder.retrievalConfig
    )

    val schedulerThread = new Thread(scheduler)
    schedulerThread.setDaemon(true)
    schedulerThread.start()

    val reader = new BufferedReader(new InputStreamReader(System.in))

    try reader.readLine
    catch {
      case ioException: IOException =>
        logger.error("Caught exception while waiting for confirm. Shutting down.", ioException)
    }

    val gracefulShutdownFuture = scheduler.startGracefulShutdown
    logger.info("Waiting up to 20 seconds for shutdown to complete.")
    try gracefulShutdownFuture.get(TWENTY, TimeUnit.SECONDS)
    catch {
      case e: InterruptedException =>
        logger.info("Interrupted while waiting for graceful shutdown. Continuing.")
      case e: ExecutionException =>
        logger.error("Exception while executing graceful shutdown.", e)
      case e: TimeoutException =>
        logger.error("Timeout while waiting for shutdown. Scheduler may not have exited.")
    }
    logger.info("Completed, shutting down now.")

  }
}
