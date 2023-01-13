package com.knoldus.aws.bootstrap

import com.knoldus.aws.models.dynamodb.QuestionTable
import com.knoldus.aws.services.dynamodb.QuestionServiceImpl
import com.knoldus.aws.services.kinesis.writer.{ BankAccountEventGenerator, BankAccountEventPublisher }
import com.typesafe.config.Config

class ServiceInstantiator(conf: Config) {
  private val tableName = conf.getString("dynamodb-table-name")
  val questionTable: QuestionTable = QuestionTable(tableName)

  lazy val questionService = new QuestionServiceImpl(questionTable)

  private lazy val bankAccountEventPublisher = new BankAccountEventPublisher(conf)
  lazy val bankAccountEventGeneratorService = new BankAccountEventGenerator(bankAccountEventPublisher)
}
