sbtPlugin := true

organization := "com.github.daniel-shuy"

name := "sbt-scripted-scalatest"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin"))

scmInfo := Some(ScmInfo(
  url("https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin"),
  "git@github.com:daniel-shuy/scripted-scalatest-sbt-plugin.git"
))

developers := List(
  Developer("daniel-shuy", "Daniel Shuy", "daniel_shuy@hotmail.com", url("https://github.com/daniel-shuy")),
)

crossSbtVersions := Seq(
  "0.13.17",
  "1.2.6"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.0"
)
