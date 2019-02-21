package com.github.daniel.shuy.sbt.scripted.scalatest

import org.scalatest._
import sbt.{Keys, Project, State}

trait ScriptedScalaTestSuiteMixin extends ScriptedScalaTestSuite with BeforeAndAfterEach {
  this: Suite =>

  val sbtState: State

  // run Clean before each test to restore project to a clean slate
  override protected def beforeEach(): Unit = {
    Project.runTask(Keys.clean, sbtState)
    super.beforeEach() // To be stackable, must call super.beforeEach
  }
}
