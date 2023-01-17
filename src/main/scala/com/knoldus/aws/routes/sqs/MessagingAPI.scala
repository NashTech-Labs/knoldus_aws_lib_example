package com.knoldus.aws.routes.sqs

import akka.http.scaladsl.server.Route

trait MessagingAPI {

  def createQueue: Route

  def listQueues: Route

  def deleteQueue(): Route

  def sendMsgToFIFOQueue: Route

  def sendMsgToStandardQueue: Route

  def sendMultipleMsgToFIFOQueue: Route

  def sendMultipleMsgToStandardQueue: Route

  def receiveMessage: Route

  def deleteMessage(): Route

  def deleteMultipleMessages(): Route

}
