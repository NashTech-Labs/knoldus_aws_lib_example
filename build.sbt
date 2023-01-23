import Dependencies._
import sbt.Test

enablePlugins(
    JavaAppPackaging,
    DockerPlugin
)

Compile / mainClass := Some("com.knoldus.aws.bootstrap.DriverApp")
Docker / packageName := "knoldus/aws-lib-examples"
dockerExposedPorts ++= Seq(8000)

resolvers += "GitHub Packages" at "https://maven.pkg.github.com/knoldus/knoldus-aws-lib"

lazy val KnoldusAwsLibSamples = Project("knoldus-aws-lib-examples", file("."))
  .settings(
    name := "knoldus-aws-lib-examples",
    organization := "knoldus",
    description := "Examples demonstrating the use of knoldus aws library",
    version := "1.0",
    ThisBuild / scalaVersion := Versions.ScalaVersion,
    ThisBuild / envFileName := ".env-knoldus-aws-sample-app",
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    libraryDependencies ++= Dependencies.Main.All ++ Dependencies.Test.All,
    scalafmtOnCompile := true,
    fork in run := false,
    parallelExecution in Test := false
  )