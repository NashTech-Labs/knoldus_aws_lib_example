package com.knoldus.aws.services.kinesis

import com.knoldus.aws.models.kinesis.{ BankAccount, BankAccountTable }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BankAccountService(bankAccountTable: BankAccountTable) {

  def getBankAccounts(count: Int): Future[Seq[BankAccount]] =
    Future {
      val records = bankAccountTable.retrieveLatestRecords(count)
      val bankAccounts = records.flatMap(BankAccount(_))
      bankAccounts
    }

}
