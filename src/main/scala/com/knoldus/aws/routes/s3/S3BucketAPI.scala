package com.knoldus.aws.routes.s3

import akka.http.scaladsl.server.Route

trait S3BucketAPI {

  def createS3Bucket: Route

  def deleteS3Bucket(): Route

  def searchS3Bucket: Route

  def listAllBuckets: Route

  def retrieveAllBucketKeys: Route

}
