import sbt._
import sbt.Keys._

object AkkaConcurrencyBuild extends Build {
  val akkaVersion = "2.1.2"
  lazy val akkaConcurrency = Project(
    id = "akka-concurrency",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Akka Concurrency",
      organization := "com.deploymentzone",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.0",
      scalacOptions ++= Seq("-feature", "-deprecation"),
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
        "org.scalatest" %% "scalatest" % "2.0.M6-SNAP9" % "test"
      )
    )
  )
}
