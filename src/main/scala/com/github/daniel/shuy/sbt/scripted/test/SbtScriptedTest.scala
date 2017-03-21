package com.github.daniel.shuy.sbt.scripted.test

import org.scalatest.Suite
import sbt.{AutoPlugin, PluginTrigger, Setting, SettingKey, TaskKey}

object SbtScriptedTest extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  sealed abstract class ScriptedTestStacks(val shortstacks: Boolean, val fullstacks: Boolean)
  case object NoStacks extends ScriptedTestStacks(false, false)
  case object ShortStacks extends ScriptedTestStacks(true, false)
  case object FullStacks extends ScriptedTestStacks(true, true)

  object autoImport {
    lazy val scriptedTestDurations: SettingKey[Boolean] = SettingKey("scripted-test-durations", "If false, will not display durations of tests.")
    lazy val scriptedTestStacks: SettingKey[ScriptedTestStacks] = SettingKey("scripted-test-stacks", "Length of stack traces to print.")
    lazy val scriptedTestStats: SettingKey[Boolean] = SettingKey("scripted-test-stats", "If false, will not display various statistics of tests.")
    lazy val scriptedTestSpec: TaskKey[Suite] = TaskKey("scripted-test-spec", "ScalaTest Spec for SBT plugin test.")
    lazy val scriptedTest: TaskKey[Unit] = TaskKey("scripted-test", "Executes all tests for SBT plugin.")
  }
  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    scriptedTestDurations := true,
    scriptedTestStacks := NoStacks,
    scriptedTestStats := true,

    scriptedTest := {
      val stacks = scriptedTestStacks.value

      scriptedTestSpec.value.execute(
        durations = scriptedTestDurations.value,
        shortstacks = stacks.shortstacks,
        fullstacks = stacks.fullstacks,
        stats = scriptedTestStats.value
      )
    }
  )
}
