package com.knoldus.aws.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.knoldus.aws.models.dynamodb.{ Question, QuestionUpdate }
import spray.json._
import com.knoldus.aws.models.s3._
import com.knoldus.aws.models.sqs._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val bucketFormat: RootJsonFormat[S3Bucket] = jsonFormat1(S3Bucket.apply)

  implicit val s3BucketResponseFormat: RootJsonFormat[S3BucketResponse] = jsonFormat1(S3BucketResponse.apply)

  implicit val s3BucketListResponseFormat: RootJsonFormat[S3BucketListResponse] = jsonFormat1(
    S3BucketListResponse.apply
  )

  implicit val queueListResponseFormat: RootJsonFormat[QueueListResponse] = jsonFormat1(
    QueueListResponse.apply
  )

  implicit val s3BucketKeysResponseFormat: RootJsonFormat[S3BucketKeysResponse] = jsonFormat1(
    S3BucketKeysResponse.apply
  )

  implicit val retrieveObjectRequestFormat: RootJsonFormat[RetrieveObjectRequest] = jsonFormat3(
    RetrieveObjectRequest.apply
  )

  implicit val s3ObjectResponseFormat: RootJsonFormat[S3ObjectResponse] = jsonFormat2(S3ObjectResponse.apply)

  implicit val copyObjectRequestFormat: RootJsonFormat[CopyObjectRequest] = jsonFormat4(CopyObjectRequest.apply)

  implicit val objectDeletionRequestFormat: RootJsonFormat[ObjectDeletionRequest] = jsonFormat2(
    ObjectDeletionRequest.apply
  )

  implicit val createQueueRequestFormat: RootJsonFormat[CreateQueueRequest] = jsonFormat2(CreateQueueRequest.apply)

  implicit val createQueueResponseFormat: RootJsonFormat[CreateQueueResponse] = jsonFormat3(CreateQueueResponse.apply)

  implicit val deleteQueueRequestFormat: RootJsonFormat[DeleteQueueRequest] = jsonFormat1(DeleteQueueRequest.apply)

  implicit val receiveMessageRequestFormat: RootJsonFormat[ReceiveMessageRequest] = jsonFormat3(
    ReceiveMessageRequest.apply
  )

  implicit val MessageResponseFormat: RootJsonFormat[MessageResponse] = jsonFormat3(MessageResponse.apply)

  implicit val SendMessagesToStandardRequestFormat: RootJsonFormat[SendMessagesToStandardRequest] = jsonFormat4(
    SendMessagesToStandardRequest.apply
  )

  implicit val sendMessageToFifoRequestFormat: RootJsonFormat[SendMessageToFifoRequest] = jsonFormat5(
    SendMessageToFifoRequest.apply
  )

  implicit val s3ObjectSummariesResponseFormat: RootJsonFormat[S3ObjectSummariesResponse] = jsonFormat7(
    S3ObjectSummariesResponse.apply
  )

  implicit val sendMessageToStandardRequestFormat: RootJsonFormat[SendMessageToStandardRequest] = jsonFormat4(
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

  implicit val copyObjectResponseFormat: RootJsonFormat[CopyObjectResponse] = jsonFormat3(
    CopyObjectResponse.apply
  )

  implicit val questionFormat: RootJsonFormat[Question] = jsonFormat4(Question.apply)
  implicit val questionUpdateFormat: RootJsonFormat[QuestionUpdate] = jsonFormat2(QuestionUpdate.apply)

}
