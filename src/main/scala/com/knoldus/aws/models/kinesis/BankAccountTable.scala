package com.knoldus.aws.models.kinesis

import com.knoldus.dynamodb.dao.DynamoTable

case class BankAccountTable(tableName: String) extends DynamoTable
