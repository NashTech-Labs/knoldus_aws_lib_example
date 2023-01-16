import Dependencies._
import sbt.Test

Compile / mainClass := Some("com.knoldus.aws.bootstrap.DriverApp")

lazy val KnoldusAwsLibSamples = Project("knoldus-aws-lib-examples", file("."))
  .settings(
    name := "knoldus-aws-lib-examples",
    organization := "knoldus",
    description := "Examples demonstrating the use of knoldus aws library",
    version := "1.0",
    ThisBuild / scalaVersion := Versions.ScalaVersion,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    libraryDependencies ++= Dependencies.Main.All ++ Dependencies.Test.All,
    scalafmtOnCompile := true,
    fork in run := false,
    parallelExecution in Test := false
  )