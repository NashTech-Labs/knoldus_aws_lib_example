package com.knoldus.aws.routes.kinesis

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.knoldus.aws.services.kinesis.BankAccountService
import com.knoldus.aws.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

class BankAccountRoutes(bankAccountService: BankAccountService) extends LazyLogging with JsonSupport {
  val routes: Route = getBankAccounts

  def getBankAccounts: Route =
    path("bank-account" / IntNumber) { count =>
      pathEnd {
        get {
          logger.info(s"Making request for getting the bank accounts")
          val response = bankAccountService.getBankAccounts(count)
          complete(response)
        }
      }
    }
}
