
organization := "ba.sake"
name := "hepek-classycle"
description := "Classycle fork"

version := "0.0.1"

scalaVersion := "2.12.3"

compileOrder := CompileOrder.JavaThenScala

libraryDependencies ++= Seq(
  //"org.specs2" % "classycle" % "1.4.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishMavenStyle := true

// publish as Java library
crossPaths := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

developers += Developer("sake92", "Sakib Hadžiavdić", "sakib@sake.ba", url("http://sake.ba"))

scmInfo := Some(ScmInfo(url("https://github.com/sake92/hepek-classycle"), "scm:git:git@github.com:sake92/hepek-classycle.git"))

homepage := Some(url("http://sake.ba"))
