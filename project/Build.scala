import sbt._
import Keys._

object ZooKey extends Build {
  val sharedSettings = Seq(
    version := "0.0.1",
    scalaVersion := "2.10.1",
    organization := "com.mosesn"
  )

  val coreSettings = Seq(
    libraryDependencies ++= Seq(
      "com.twitter" %% "ostrich" % "9.1.0" cross CrossVersion.binary,
      "com.twitter" %% "util-logging" % "6.3.0" cross CrossVersion.binary,
      "com.twitter" %% "util-zk" % "6.3.0" cross CrossVersion.binary excludeAll(
        ExclusionRule(organization = "com.sun.jdmk"),
        ExclusionRule(organization = "com.sun.jmx"),
        ExclusionRule(organization = "javax.jms")
      ),
      "com.twitter" %% "finagle-core" % "6.3.0" cross CrossVersion.binary,
      "org.scalatest" %% "scalatest" % "1.9.1" % "test" cross CrossVersion.binary,
      "org.mockito" % "mockito-all" % "1.9.5" % "test",
      "org.apache.curator" % "curator-test" % "2.0.0-incubating" % "test"
    )
  )

  val httpSettings = Seq(
    libraryDependencies += "com.twitter" %% "finagle-http" % "6.3.0" cross CrossVersion.binary
  )

  lazy val zookey = Project(
    id = "zookey",
    base = file(".")
  )
  .settings(sharedSettings: _*)
  .aggregate(core, http)

  lazy val core = quickProject("core")
    .settings(sharedSettings: _*)
    .settings(coreSettings: _*)

  lazy val http = quickProject("http")
    .settings(sharedSettings: _*)
    .settings(httpSettings: _*)
    .dependsOn(core % "compile->compile;test->test")

  def quickProject(string: String): Project = {
    val str = "zookey-%s".format(string)
    Project(
      id = str,
      base = file(str)
    )
  }
}
