package com.knoldus.aws.routes.kinesis

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.knoldus.aws.models.kinesis.{ BankAccountCreationEventRequest, UpdateBankAccountEventRequest }
import com.knoldus.aws.services.kinesis.writer.BankAccountEventGenerator
import com.knoldus.aws.utils.JsonSupport
import com.typesafe.scalalogging.LazyLogging

class BankAccountEventGeneratorRoutes(bankAccountEventGenerator: BankAccountEventGenerator)
    extends JsonSupport
    with LazyLogging {
  val routes: Route = createBankAccountEvent ~ creditBankAccountEvent ~ debitBankAccountEvent

  def createBankAccountEvent: Route =
    path("event" / "bank-account" / "create") {
      pathEnd {
        (post & entity(as[BankAccountCreationEventRequest])) { bankAccountCreationRequest =>
          logger.info("Making event request for bank account creation")
          val response = bankAccountEventGenerator.createBankAccountEvent(bankAccountCreationRequest)
          complete(response)
        }
      }
    }

  def creditBankAccountEvent: Route =
    path("event" / "bank-account" / "credit") {
      pathEnd {
        (put & entity(as[UpdateBankAccountEventRequest])) { creditBankAccountEventRequest =>
          logger.info("Making event request for crediting a bank account")
          val response = bankAccountEventGenerator.creditBankAccountEvent(
            creditBankAccountEventRequest.accountNumber,
            creditBankAccountEventRequest.amount
          )
          complete(response)
        }
      }
    }

  def debitBankAccountEvent: Route =
    path("event" / "bank-account" / "debit") {
      pathEnd {
        (put & entity(as[UpdateBankAccountEventRequest])) { debitBankAccountEventRequest =>
          logger.info("Making event request for debiting a bank account")
          val response = bankAccountEventGenerator.debitBankAccountEvent(
            debitBankAccountEventRequest.accountNumber,
            debitBankAccountEventRequest.amount
          )
          complete(response)
        }
      }
    }
}
