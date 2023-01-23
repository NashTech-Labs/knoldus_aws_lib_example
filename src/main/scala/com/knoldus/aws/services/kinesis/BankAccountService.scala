package com.knoldus.aws.services.kinesis

import com.knoldus.aws.models.kinesis.{ BankAccount, BankAccountTable }
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BankAccountService(bankAccountTable: BankAccountTable) extends LazyLogging {

  def getBankAccounts(count: Int): Future[Seq[BankAccount]] =
    Future {
      logger.info("Getting latest records from bank account table")
      val records = bankAccountTable.retrieveLatestRecords(count)
      val bankAccounts = records.flatMap(BankAccount(_))
      bankAccounts
    }

}
