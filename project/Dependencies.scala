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
    val LogbackVersion = "1.2.9"

    // Example Specific
    val DynamoDbVersion = "1.0"
    val KinesisVersion = "1.0"
    val s3Version = "1.0"
    val sqsVersion = "1.0"
    val KnoldusAwsVersion = "1.0"

    val kclVersion = "2.3.0"

    // Logging
    val ScalaLoggingVersion = "3.9.5"

    // Test
    val ScalaTestVersion = "3.2.14"
    val MockitoScalaVersion = "1.17.12"
  }

  object Main {
    val PlayJson = "com.typesafe.play" %% "play-json" % PlayJsonVersion
    val ScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion
    val AkkaActor = "com.typesafe.akka" %% "akka-actor" % AkkaVersion
    val AkkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
    val AkkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
    val AkkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
    val AkkaHttpCors =  "ch.megard" %% "akka-http-cors" % AkkaHttpCorsVersion
    val AkkaHttpTestKit = "com.typesafe.akka" % "akka-http-testkit_2.13" % AkkaHttpVersion
    val Logback = "ch.qos.logback" % "logback-classic" % LogbackVersion

    val KnoldusAwsDynamoDb = "knoldus" % "dynamodb-service_2.13" % DynamoDbVersion
    val KnoldusAwsKinesis = "knoldus" % "kinesis-service_2.13" % KinesisVersion
    val KnoldusAwsS3 = "knoldus" % "s3-service_2.13" % s3Version
    val KnoldusAwsSQS = "knoldus" % "sqs-service_2.13" % sqsVersion
    val KnoldusAws = "knoldus" % "knoldus_aws_lib_2.13" % KnoldusAwsVersion

    val awsKCL = "software.amazon.kinesis" % "amazon-kinesis-client" % kclVersion

    val All: Seq[ModuleID] = Seq(
      ScalaLogging,
      PlayJson,
      AkkaActor,
      AkkaStream,
      AkkaHttp,
      AkkaHttpSpray,
      AkkaHttpCors,
      AkkaHttpTestKit,
      KnoldusAws,
      Logback,
      awsKCL
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
