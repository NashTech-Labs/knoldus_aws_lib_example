package com.knoldus.aws.bootstrap

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.RouteConcatenation._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.model.{HttpHeaderRange, HttpOriginMatcher}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.knoldus.aws.routes.dynamodb.QuestionAPIImpl
import com.knoldus.aws.routes.kinesis.{BankAccountEventGeneratorRoutes, BankAccountRoutes}
import com.knoldus.aws.routes.s3.{DataMigrationAPIImpl, S3BucketAPIImpl}
import com.knoldus.aws.routes.sqs.MessagingAPIImpl

class RoutesInstantiator(
  services: ServiceInstantiator
) {

  private val questionAPIRoutes =
    new QuestionAPIImpl(services.questionService)

  private val bankAccountEventRoutes = new BankAccountEventGeneratorRoutes(services.bankAccountEventGeneratorService)

  private val bankAccountRoutes = new BankAccountRoutes(services.bankAccountService)

  private val s3BucketRoutes =
    new S3BucketAPIImpl(services.s3BucketService)

  private val dataMigrationAPIImplRoutes: DataMigrationAPIImpl =
    new DataMigrationAPIImpl(services.dataMigrationServiceImpl, services.s3BucketService)

  private val messagingAPIImplRoutes: MessagingAPIImpl =
    new MessagingAPIImpl(services.messagingService)

  private val corsSettings = CorsSettings.defaultSettings
    .withAllowedMethods(scala.collection.immutable.Seq(GET, POST, PUT, DELETE))
    .withAllowedOrigins(HttpOriginMatcher.*)
    .withAllowedHeaders(HttpHeaderRange.*)
    .withAllowCredentials(false)
    .withAllowGenericHttpRequests(true)

  val routes: Route = cors(corsSettings) {
    concat(
      questionAPIRoutes.routes,
      bankAccountEventRoutes.routes,
      bankAccountRoutes.routes,
      s3BucketRoutes.routes,
      dataMigrationAPIImplRoutes.routes,
      messagingAPIImplRoutes.routes
    )
  }
}
