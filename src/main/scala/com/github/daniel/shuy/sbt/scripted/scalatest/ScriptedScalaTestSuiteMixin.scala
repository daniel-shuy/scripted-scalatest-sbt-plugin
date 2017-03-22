package com.github.daniel.shuy.sbt.scripted.scalatest

import org.scalatest.{BeforeAndAfter, Suite}
import sbt.{Keys, Project, State}

trait ScriptedScalaTestSuiteMixin extends BeforeAndAfter {
  this: Suite =>

  val sbtState: State

  // run Clean before each test to restore project to a clean slate
  before {
    Project.runTask(Keys.clean, sbtState)
  }
}
