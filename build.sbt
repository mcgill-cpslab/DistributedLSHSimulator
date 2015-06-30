name := "DistributedLSHSimulator"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-contrib_2.11" % "2.3.8",
  "com.typesafe" % "config" % "1.2.0",
  "com.twitter" % "chill_2.11" % "0.6.0")
    