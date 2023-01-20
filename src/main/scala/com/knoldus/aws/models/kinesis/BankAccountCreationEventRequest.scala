package com.knoldus.aws.models.kinesis

case class BankAccountCreationEventRequest(
  accountOwner: String,
  accountType: String,
  securityCode: String,
  initialBalance: Double
)
