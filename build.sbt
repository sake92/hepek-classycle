
organization := "ba.sake"
name := "hepek-classycle"
description := "Classycle fork"

version := "0.0.2-SNAPSHOT"

publishMavenStyle := true   // publish as Java library
crossPaths := false         // isn't Scala
autoScalaLibrary := false   // don't need Scala to compile

mainClass in Compile := Some("classycle.Analyser")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

licenses += ("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

scmInfo := Some(ScmInfo(url("https://github.com/sake92/hepek-classycle"), "scm:git:git@github.com:sake92/hepek-classycle.git"))

developers += Developer("sake92", "Sakib Hadžiavdić", "sakib@sake.ba", url("http://sake.ba"))

homepage := Some(url("http://sake.ba"))
