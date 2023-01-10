package com.knoldus.aws.bootstrap

import com.knoldus.aws.models.dynamodb.QuestionTable
import com.knoldus.aws.services.dynamodb.QuestionServiceImpl
import com.knoldus.aws.services.s3.S3BucketServiceImpl
import com.knoldus.aws.services.sqs.MessagingServiceImpl
import com.knoldus.common.models.AWSConfig
import com.knoldus.s3.models.{ Configuration, S3Config }
import com.knoldus.sqs.models.{ SQSConfig, SQSEndpoint }
import com.typesafe.config.Config

class ServiceInstantiator(conf: Config) {
  //private val tableName = conf.getString("dynamodb-table-name")
  // val questionTable: QuestionTable = QuestionTable(tableName)

  //lazy val questionService = new QuestionServiceImpl(questionTable)

  private val accessKey: String = conf.getString("aws-access-key")
  private val secretKey: String = conf.getString("aws-secret-key")
  private val region: String = conf.getString("aws-region")
  private val s3ServiceEndpoint: String = conf.getString("aws-s3-serviceEndpoint")
  private val sqsServiceEndpoint: String = conf.getString("aws-sqs-serviceEndpoint")

  val awsConfig: AWSConfig = AWSConfig(accessKey, secretKey, region)
  private val s3config: Configuration = Configuration(awsConfig, S3Config(s3ServiceEndpoint))
  private val sqsConfig: SQSConfig = SQSConfig(awsConfig, SQSEndpoint(sqsServiceEndpoint))

  lazy val s3BucketService = new S3BucketServiceImpl(s3config)
  lazy val messagingService = new MessagingServiceImpl(sqsConfig)
}
