package com.knoldus.aws.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import com.knoldus.aws.models.s3._
import com.knoldus.aws.models.sqs._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val bucketFormat: RootJsonFormat[S3Bucket] = jsonFormat1(S3Bucket.apply)

  implicit val s3BucketResponseFormat: RootJsonFormat[S3BucketResponse] = jsonFormat1(S3BucketResponse.apply)

  implicit val s3BucketListResponseFormat: RootJsonFormat[S3BucketListResponse] = jsonFormat1(
    S3BucketListResponse.apply
  )

  implicit val retrieveObjectRequestFormat: RootJsonFormat[RetrieveObjectRequest] = jsonFormat4(
    RetrieveObjectRequest.apply
  )

  implicit val copyObjectRequestFormat: RootJsonFormat[CopyObjectRequest] = jsonFormat4(CopyObjectRequest.apply)

  implicit val objectDeletionRequestFormat: RootJsonFormat[ObjectDeletionRequest] = jsonFormat2(
    ObjectDeletionRequest.apply
  )

  implicit val createQueueRequestFormat: RootJsonFormat[CreateQueueRequest] = jsonFormat2(CreateQueueRequest.apply)

  implicit val deleteQueueRequestFormat: RootJsonFormat[DeleteQueueRequest] = jsonFormat1(DeleteQueueRequest.apply)

  implicit val receiveMessageRequestFormat: RootJsonFormat[ReceiveMessageRequest] = jsonFormat1(
    ReceiveMessageRequest.apply
  )

  implicit val MessageResponseFormat: RootJsonFormat[MessageResponse] = jsonFormat2(MessageResponse.apply)

  implicit val SendMessagesToStandardRequestFormat: RootJsonFormat[SendMessagesToStandardRequest] = jsonFormat3(
    SendMessagesToStandardRequest.apply
  )

  implicit val sendMessageToFifoRequestFormat: RootJsonFormat[SendMessageToFifoRequest] = jsonFormat5(
    SendMessageToFifoRequest.apply
  )

  implicit val s3ObjectResponseFormat: RootJsonFormat[S3ObjectResponse] = jsonFormat7(
    S3ObjectResponse.apply
  )

  implicit val sendMessageToStandardRequestFormat: RootJsonFormat[SendMessageToStandardRequest] = jsonFormat3(
    SendMessageToStandardRequest.apply
  )

  implicit val retrieveObjectSummariesRequestFormat: RootJsonFormat[RetrieveObjectSummariesRequest] = jsonFormat2(
    RetrieveObjectSummariesRequest.apply
  )

  implicit val deleteMessageRequestFormat: RootJsonFormat[DeleteMessageRequest] = jsonFormat2(
    DeleteMessageRequest.apply
  )

  implicit val deleteMessagesRequestFormat: RootJsonFormat[DeleteMessagesRequest] = jsonFormat2(
    DeleteMessagesRequest.apply
  )

  implicit val sendMessagesToFifoRequestFormat: RootJsonFormat[SendMessagesToFifoRequest] = jsonFormat5(
    SendMessagesToFifoRequest.apply
  )

  implicit val retrieveBucketKeysResponseFormat: RootJsonFormat[RetrieveBucketKeysRequest] = jsonFormat2(
    RetrieveBucketKeysRequest.apply
  )

  implicit val copyObjectResponseFormat: RootJsonFormat[CopyObjectResponse] = jsonFormat4(
    CopyObjectResponse.apply
  )

}
