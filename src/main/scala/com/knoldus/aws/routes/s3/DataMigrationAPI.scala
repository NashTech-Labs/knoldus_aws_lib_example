package com.knoldus.aws.routes.s3

import akka.http.scaladsl.server.Route

trait DataMigrationAPI {

  def uploadFileToS3: Route

  def retrieveFile: Route

  def copyFile: Route

  def deleteFile(): Route

  def getAllObjects: Route
}
