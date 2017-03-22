package com.github.daniel.shuy.sbt.scripted.scalatest

import org.scalatest.Suite
import sbt.{AutoPlugin, PluginTrigger, Setting, SettingKey, TaskKey}

object SbtScriptedScalaTest extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  sealed abstract class ScriptedTestStacks(val shortstacks: Boolean, val fullstacks: Boolean)
  case object NoStacks extends ScriptedTestStacks(false, false)
  case object ShortStacks extends ScriptedTestStacks(true, false)
  case object FullStacks extends ScriptedTestStacks(true, true)

  object autoImport {
    lazy val scriptedScalaTestDurations: SettingKey[Boolean] = SettingKey("scripted-scalatest-durations", "If false, will not display durations of tests.")
    lazy val scriptedScalaTestStacks: SettingKey[ScriptedTestStacks] = SettingKey("scripted-scalatest-stacks", "Length of stack traces to print.")
    lazy val scriptedScalaTestStats: SettingKey[Boolean] = SettingKey("scripted-scalatest-stats", "If false, will not display various statistics of tests.")
    lazy val scriptedScalaTestSpec: TaskKey[Suite with ScriptedScalaTestSuiteMixin] = TaskKey("scripted-scalatest-spec", "The ScalaTest Spec.")
    lazy val scriptedScalaTest: TaskKey[Unit] = TaskKey("scripted-scalatest", "Executes all ScalaTest tests for SBT plugin.")
  }
  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    scriptedScalaTestDurations := true,
    scriptedScalaTestStacks := NoStacks,
    scriptedScalaTestStats := true,

    scriptedScalaTest := {
      val stacks = scriptedScalaTestStacks.value

      scriptedScalaTestSpec.value.execute(
        durations = scriptedScalaTestDurations.value,
        shortstacks = stacks.shortstacks,
        fullstacks = stacks.fullstacks,
        stats = scriptedScalaTestStats.value
      )
    }
  )
}
