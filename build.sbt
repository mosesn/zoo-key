name := "zookey"

version := "0.0.1"

scalaVersion := "2.10.1"

organization := "com.mosesn"

libraryDependencies ++= Seq(
  "com.twitter" %% "ostrich" % "9.1.0" cross CrossVersion.binary,
  "com.twitter" %% "util-logging" % "6.3.0" cross CrossVersion.binary,
  "com.twitter" %% "util-zk" % "6.3.0" cross CrossVersion.binary excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
  ),
  "com.twitter" %% "finagle-http" % "6.3.0" cross CrossVersion.binary,
  "org.scalatest" %% "scalatest" % "1.9.1" % "test" cross CrossVersion.binary
)

