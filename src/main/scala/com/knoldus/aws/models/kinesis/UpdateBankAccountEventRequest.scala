package com.knoldus.aws.models.kinesis

import java.util.UUID

case class UpdateBankAccountEventRequest(accountNumber: UUID, amount: Double)
