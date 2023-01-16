package com.knoldus.aws.utils

object Constants {

  final val ZERO = 0
  final val TEN = 10
  final val EMPTY_ARRAY = "[]"

  final val LOCALSTACK = "http://localhost:4566"

  final val BUCKET_CREATED = "The S3 bucket has been successfully created."
  final val BUCKET_DELETED = "The S3 bucket has been successfully deleted."
  final val BUCKET_ALREADY_EXISTS = "S3 bucket with the same name already exists."
  final val BUCKET_CREATION_EXCEPTION = "An exception encountered while creating the S3 bucket."
  final val BUCKET_DELETION_EXCEPTION = "An exception encountered while deleting the S3 bucket."
  final val BUCKET_LISTING_EXCEPTION = "An exception encountered while listing all the S3 buckets."
  final val RETRIEVING_ALL_KEYS_EXCEPTION = "An exception encountered while retrieving all the S3 bucket keys."
  final val BUCKET_NOT_FOUND = "The specified S3 bucket does not exist."
  final val KEY_NOT_FOUND = "The specified S3 bucket key does not exist."
  final val OBJECT_DELETED = "The specified S3 object has been deleted successfully."
  final val ALL_OBJECTS_DELETED = "All the S3 objects in the specified S3 Bucket have been deleted successfully."

  final val OBJECT_UPLOADED = "The specified object is uploaded to the S3 bucket successfully."
  final val OBJECT_UPLOADING_EXCEPTION = "An exception encountered while uploading the object in the S3 bucket."
  final val OBJECT_RETRIEVAL_EXCEPTION = "An exception encountered while retrieving the object from the S3 bucket."
  final val OBJECT_COPYING_EXCEPTION = "An exception encountered while copying the object in the S3 bucket."
  final val OBJECT_DELETION_EXCEPTION = "An exception encountered while deleting the object from the S3 bucket."

  final val RETRIEVING_ALL_OBJECTS_EXCEPTION =
    "An exception encountered while retrieving all the objects from the S3 bucket."

  final val QUEUE_DELETED = "The queue has been successfully deleted."
  final val MESSAGE_SENT = "The message has been successfully sent."
  final val MESSAGES_SENT = "The messages have been successfully sent."
  final val MESSAGE_DELETED = "The message has been successfully deleted."
  final val MESSAGES_DELETED = "All the messages has been successfully deleted."
  final val QUEUE_NOT_FOUND = "The specified queue is does not exist."

  final val QUEUE_CREATION_EXCEPTION = "An exception encountered while creating a new queue."
  final val QUEUE_DELETION_EXCEPTION = "An exception encountered while deleting a queue."
  final val SEND_MSG_TO_FIFO_EXCEPTION = "An exception encountered while sending a message to fifo queue."
  final val SEND_MSGS_TO_FIFO_EXCEPTION = "An exception encountered while sending multiple messages to fifo queue."
  final val SEND_MSG_TO_STANDARD_EXCEPTION = "An exception encountered while sending a message to standard queue."

  final val SEND_MSGS_TO_STANDARD_EXCEPTION =
    "An exception encountered while sending multiple messages to standard queue."
  final val MESSAGE_DELETION_EXCEPTION = "An exception encountered while deleting a message from the queue."
  final val MESSAGES_DELETION_EXCEPTION = "An exception encountered while deleting multiple messages from the queue."
  final val MESSAGE_RECEIVING_EXCEPTION = "An exception encountered while receiving a message from the queue."

}
