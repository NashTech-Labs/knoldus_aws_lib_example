package com.knoldus.aws.services.kinesis.processor

import com.typesafe.scalalogging.LazyLogging
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.{ConfigsBuilder, KinesisClientUtil}
import software.amazon.kinesis.coordinator.Scheduler

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.util.UUID
import java.util.concurrent.{ExecutionException, TimeUnit, TimeoutException}

class BankAccountEventConsumer(streamName: String, applicationName: String, region: Region, tableName: String) extends LazyLogging{

  private val kinesisClient: KinesisAsyncClient = KinesisClientUtil.createKinesisAsyncClient(KinesisAsyncClient.builder.region(region))

  private def run(): Unit = {

    val dynamoClient = DynamoDbAsyncClient.builder.region(region).build
    val cloudWatchClient = CloudWatchAsyncClient.builder.region(region).build
    val configsBuilder = new ConfigsBuilder(streamName, applicationName, kinesisClient, dynamoClient, cloudWatchClient, UUID.randomUUID.toString, new BankAccountEventProcessorFactory(tableName))

    val scheduler = new Scheduler(configsBuilder.checkpointConfig, configsBuilder.coordinatorConfig, configsBuilder.leaseManagementConfig, configsBuilder.lifecycleConfig, configsBuilder.metricsConfig, configsBuilder.processorConfig, configsBuilder.retrievalConfig)

    val schedulerThread = new Thread(scheduler)
    schedulerThread.setDaemon(true)
    schedulerThread.start()

    System.out.println("Press enter to shutdown the consumer")
    val reader = new BufferedReader(new InputStreamReader(System.in))

    try reader.readLine
    catch {
      case ioException: IOException =>
        logger.error("Caught exception while waiting for confirm. Shutting down.", ioException)
    }

    val gracefulShutdownFuture = scheduler.startGracefulShutdown
    logger.info("Waiting up to 20 seconds for shutdown to complete.")
    try gracefulShutdownFuture.get(20, TimeUnit.SECONDS)
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
