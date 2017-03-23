sbtPlugin := true

organization := "com.github.daniel-shuy"

name := "sbt-scripted-scalatest"

version := "0.1.0"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin"))

publishMavenStyle := false

bintrayRepository := "sbt-plugins"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1"
)
