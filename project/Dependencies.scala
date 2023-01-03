import sbt._
import Dependencies.Versions._


object Dependencies {

  object Versions {
    // System
    val ScalaVersion = "2.13.8"

    // General
    val PlayJsonVersion = "2.9.3"
    val TypeSafeConfigVersion = "1.4.2"
    val PureConfigVersion = "0.17.2"
    val AwsJavaSDKVersion = "1.11.490"
    val AkkaHttpVersion = "10.4.0"
    val AkkaHttpCorsVersion = "1.1.3"

    // Example Specific
    val DynamoDbVersion = "1.0"
    val KinesisVersion = "1.0"
    val s3Version = "1.0"
    val sqsVersion = "1.0"

    // Logging
    val ScalaLoggingVersion = "3.9.5"

    // Test
    val ScalaTestVersion = "3.2.14"
    val MockitoScalaVersion = "1.17.12"
  }

  object Main {
    val PlayJson = "com.typesafe.play" %% "play-json" % PlayJsonVersion
    val PureConfig = "com.github.pureconfig" %% "pureconfig" % PureConfigVersion
    val ScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion
    val AkkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
    val AkkaHttpCors =  "ch.megard" %% "akka-http-cors" % AkkaHttpCorsVersion
    val AkkaHttpTestKit = "com.typesafe.akka" % "akka-http-testkit_2.13" % AkkaHttpVersion
    val Dynamo = "knoldus" % "dynamodb-service_2.13" % DynamoDbVersion
    val Kinesis = "knoldus" % "kinesis-service_2.13" % KinesisVersion
    val s3 = "knoldus" % "s3-service_2.13" % s3Version
    val sqs = "knoldus" % "sqs-service_2.13" % sqsVersion

    val All: Seq[ModuleID] = Seq(
      ScalaLogging,
      PlayJson,
      PureConfig,
      AkkaHttp,
      AkkaHttpCors,
      AkkaHttpTestKit,
      Dynamo,
      Kinesis,
      s3,
      sqs
    )
  }

  object Test {
    val ScalaTest = "org.scalatest" % "scalatest_2.13" % ScalaTestVersion
    val MockitoScala = "org.mockito" % "mockito-scala_2.13" % MockitoScalaVersion

    val All: Seq[ModuleID] = Seq(
      ScalaTest,
      MockitoScala
    ).map(_ % Configurations.Test)
  }
}
