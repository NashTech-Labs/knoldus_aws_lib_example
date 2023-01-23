import sbt._
import Dependencies.Versions._


object Dependencies {

  object Versions {
    // System
    val ScalaVersion = "2.13.8"

    // General
    val PlayJsonVersion = "2.9.3"
    val TypeSafeConfigVersion = "1.4.2"
    val AwsJavaSDKVersion = "1.11.490"
    val AkkaVersion = "2.7.0"
    val AkkaHttpVersion = "10.4.0"
    val AkkaHttpCorsVersion = "1.1.3"

    // Example Specific
    val DynamoDbVersion = "0.1"
    val KinesisVersion = "0.1"
    val s3Version = "0.1"
    val sqsVersion = "0.1"
    val KnoldusAwsVersion = "0.1"

    val kclVersion = "2.3.0"

    // Logging
    val ScalaLoggingVersion = "3.9.5"
    val Slf4jVersion = "1.7.25"

    // Test
    val ScalaTestVersion = "3.2.14"
    val MockitoScalaVersion = "1.17.12"
  }

  object Main {
    val PlayJson = "com.typesafe.play" %% "play-json" % PlayJsonVersion
    val ScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion
    val AkkaActor = "com.typesafe.akka" %% "akka-actor" % AkkaVersion
    val AkkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
    val Slf4j = "org.slf4j" % "slf4j-simple" % Slf4jVersion
    lazy val macWire = "com.softwaremill.macwire" %% "macros" % "2.5.7"
    val AkkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
    val AkkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
    val AkkaHttpCors =  "ch.megard" %% "akka-http-cors" % AkkaHttpCorsVersion
    val AkkaHttpTestKit = "com.typesafe.akka" % "akka-http-testkit_2.13" % AkkaHttpVersion

    val KnoldusAwsDynamoDb = "knoldus" % "dynamodb-service_2.13" % DynamoDbVersion
    val KnoldusAwsKinesis = "knoldus" % "kinesis-service_2.13" % KinesisVersion
    val KnoldusAwsS3 = "knoldus" % "s3-service_2.13" % s3Version
    val KnoldusAwsSQS = "knoldus" % "sqs-service_2.13" % sqsVersion
    val KnoldusAws = "knoldus" % "knoldus_aws_lib_2.13" % KnoldusAwsVersion

    val awsKCL = "software.amazon.kinesis" % "amazon-kinesis-client" % kclVersion

    val All: Seq[ModuleID] = Seq(
      ScalaLogging,
      PlayJson,
      macWire,
      AkkaActor,
      AkkaStream,
      AkkaHttp,
      AkkaHttpSpray,
      AkkaHttpCors,
      AkkaHttpTestKit,
      KnoldusAws,
      awsKCL,
      Slf4j
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
