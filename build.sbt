sbtPlugin := true

organization := "com.github.daniel-shuy"

name := "sbt-scripted-scalatest"

version := "1.1.0-SNAPSHOT"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin"))

crossSbtVersions := Seq(
  "0.13.16",
  "1.1.0"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4"
)
