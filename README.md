# scripted-scalatest-sbt-plugin

[ ![Download](https://api.bintray.com/packages/daniel-shuy/sbt-plugins/sbt-scripted-scalatest/images/download.svg) ](https://bintray.com/daniel-shuy/sbt-plugins/sbt-scripted-scalatest/_latestVersion)

| Branch  | Travis CI                                                                                                                                                              | CodeFactor                                                                                                                                                                                                                   | Codacy                                                                                                                                                                                                                                                                                                       | Better Code Hub                                                                                                                                |
| ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| Master  | [![Build Status](https://travis-ci.org/daniel-shuy/scripted-scalatest-sbt-plugin.svg?branch=master)](https://travis-ci.org/daniel-shuy/scripted-scalatest-sbt-plugin)  | [![CodeFactor](https://www.codefactor.io/repository/github/daniel-shuy/scripted-scalatest-sbt-plugin/badge/master)](https://www.codefactor.io/repository/github/daniel-shuy/scripted-scalatest-sbt-plugin/overview/master)   | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/244276b4573e4ae899443fa79c34822b?branch=master)](https://www.codacy.com/app/daniel-shuy/scripted-scalatest-sbt-plugin?utm_source=github.com&utm_medium=referral&utm_content=daniel-shuy/scripted-scalatest-sbt-plugin&utm_campaign=Badge_Grade)  | [![BCH compliance](https://bettercodehub.com/edge/badge/daniel-shuy/scripted-scalatest-sbt-plugin?branch=master)](https://bettercodehub.com/)  |
| Develop | [![Build Status](https://travis-ci.org/daniel-shuy/scripted-scalatest-sbt-plugin.svg?branch=develop)](https://travis-ci.org/daniel-shuy/scripted-scalatest-sbt-plugin) | [![CodeFactor](https://www.codefactor.io/repository/github/daniel-shuy/scripted-scalatest-sbt-plugin/badge/develop)](https://www.codefactor.io/repository/github/daniel-shuy/scripted-scalatest-sbt-plugin/overview/develop) | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/244276b4573e4ae899443fa79c34822b?branch=develop)](https://www.codacy.com/app/daniel-shuy/scripted-scalatest-sbt-plugin?utm_source=github.com&utm_medium=referral&utm_content=daniel-shuy/scripted-scalatest-sbt-plugin&utm_campaign=Badge_Grade) | [![BCH compliance](https://bettercodehub.com/edge/badge/daniel-shuy/scripted-scalatest-sbt-plugin?branch=develop)](https://bettercodehub.com/) |

| Plugin Version | SBT Version   | ScalaTest Version |
| -------------- | ------------- | ----------------- |
| 1.x.x          | 0.13.x, 1.x.x | 3.0.x             |
| 2.x.x          | 0.13.x, 1.x.x | 3.1.x+            |

A SBT plugin to use [ScalaTest](http://www.scalatest.org/) with scripted-plugin to test your SBT plugins

Traditionally, to test a SBT plugin, you had to create subprojects in `/sbt-test`, then in the subprojects, create SBT tasks to perform the testing, then specify the tasks to execute in a `test` file (see <http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html>).

This is fine when performing simple tests, but for complicated tests (see <http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+6%3A+custom+assertion>), this can get messy really quickly:

-   It sucks to not be able to write tests in a BDD style (except by using comments, which feels clunky).
-   Manually writing code to print the test results to the console for each subproject is a pain.

This plugin leverages ScalaTest's powerful assertion system (to automatically print useful messages on assertion failure) and its expressive DSLs.

This plugin allows you to use any of ScalaTest's test [Suites](http://www.scalatest.org/user_guide/selecting_a_style), including [AsyncTestSuites](http://www.scalatest.org/user_guide/async_testing).

## Notes

-   Do not use ScalaTest's [ParallelTestExecution](http://doc.scalatest.org/3.0.0/index.html#org.scalatest.ParallelTestExecution) mixin with this plugin. `ScriptedScalaTestSuiteMixin` runs `sbt clean` before each test, which may cause weird side effects when run in parallel.
-   When executing SBT tasks in tests, use `Project.runTask(<task>, state.value)` instead of `<task>.value`. Calling `<task>.value` declares it as a dependency, which executes before the tests, not when the line is called.
-   When implementing [BeforeAndAfterEach](http://doc.scalatest.org/3.0.0/index.html#org.scalatest.BeforeAndAfterEach)'s `beforeEach`, make sure to invoke `super.beforeEach` afterwards:

```scala
override protected def beforeEach(): Unit = {
  // ...
  super.beforeEach() // To be stackable, must call super.beforeEach
}
```

-   This SBT plugin is now tested using itself!

## Usage

### Step 1: Include the scripted-plugin in your build

Add the following to your main project's `project/scripted.sbt` (create file it if doesn't exist):

#### SBT 0.13 (<http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+2%3A+scripted-plugin>)

```scala
libraryDependencies += { "org.scala-sbt" % "scripted-plugin" % sbtVersion.value }
```

#### SBT 1.0.x-1.1.x

```scala
libraryDependencies += { "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value }
```

Note the %% operator.

#### SBT 1.2.x+

Not Required

### Step 2: Configure scripted-plugin

Recommended settings by SBT:

#### SBT 0.13 (<http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+2%3A+scripted-plugin>)

```scala
// build.sbt
ScriptedPlugin.scriptedSettings
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false
```

If you are using [sbt-cross-building](https://github.com/jrudolph/sbt-cross-building) (SBT &lt; 0.13.6), don't add scripted-plugin to `project/scripted.sbt`, and replace `ScriptedPlugin.scriptedSettings` in `build.sbt` with `CrossBuilding.scriptedSettings`.

#### SBT 1.0.x-1.1.x

```scala
// build.sbt
lazy val root = (project in file("."))
  .settings(
    name := "sbt-something",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )
```

#### SBT 1.2.x+ (<http://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html#step+2%3A+scripted-plugin>)

```scala
// build.sbt
lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-something",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )
```

### Step 3: Create the test subproject

Create the test subproject in `sbt-test/<test-group>/<test-name>`.

Include your plugin in the build.

See <http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+3%3A+src%2Fsbt-test> for an example.

### Step 4: Include sbt-scripted-scalatest in your build

Add the following to your `sbt-test/<test-group>/<test-name>/project/plugins.sbt`:

```scala
addSbtPlugin("com.github.daniel-shuy" % "sbt-scripted-scalatest" % "1.1.1")
```

Override the `scalatest` dependency version with the version of ScalaTest you wish to use:

```scala
addSbtPlugin("com.github.daniel-shuy" % "sbt-scripted-scalatest" % "1.1.1")
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"
```

### Step 5: Configure `test` script

Put **only** the following in the `sbt-test/<test-group>/<test-name>/test` script file:

`> scriptedScalatest`

### Step 6: Configure project settings for the plugin

In `sbt-test/<test-group>/<test-name>/build.sbt`, create a new ScalaTest Suite/Spec, mixin `ScriptedScalaTestSuiteMixin` and pass it into `scriptedScalaTestSpec`. When mixing in `ScriptedScalaTestSuiteMixin`, implement `sbtState` as `state.value`.

Using SBT's Example in <http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+6%3A+custom+assertion>:

```scala
import com.github.daniel.shuy.sbt.scripted.scalatest.ScriptedScalaTestSuiteMixin
import org.scalatest.Assertions._
import org.scalatest.wordspec.AnyWordSpec

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.10.6",
    assemblyJarName in assembly := "foo.jar",

    scriptedScalaTestSpec := Some(new AnyWordSpec with ScriptedScalaTestSuiteMixin {
      override val sbtState: State = state.value

      "assembly" should {
        "create a JAR that prints out 'hello'" in {
          Project.runTask(Keys.assembly, sbtState)
          val process = sbt.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
          val out = (process!!)
          assert(out.trim == "bye")
        }
      }
    }
  )
```

It is possible move the ScalaTest Suite/Spec into a separate `.scala` file in the `project` folder, however that may cause issues when trying to access SBT `SettingKey`s or declaring custom `TaskKey`s, therefore is currently not recommended except for extremely simple tests. A better approach would be to move all configurations related to this plugin to a new `.sbt` file, eg. `test.sbt`.

See [Settings](#settings) for other configurable settings.

### Step 7: Use the scripted-plugin as usual

Append `-SNAPSHOT` to the main project `version` before running `scripted-plugin`.

Eg. Run `sbt scripted` on the main project to execute all tests.

## Settings

| Setting                    | Type                                           | Description                                                                                                                                                                                                                     |
| -------------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| scriptedScalaTestSpec      | Option[Suite with ScriptedScalaTestSuiteMixin] | **Required**. The ScalaTest Suite/Spec. If not configured (defaults to `None`), no tests will be executed.                                                                                                                      |
| scriptedScalaTestDurations | Boolean                                        | **Optional**. If `true`, displays durations of tests. Defaults to `true`.                                                                                                                                                       |
| scriptedScalaTestStacks    | NoStacks / ShortStacks / FullStacks            | **Optional**. The length of stack traces to display for failed tests. `NoStacks` will not display any stack traces. `ShortStacks` displays short stack traces. `FullStacks` displays full stack traces. Defaults to `NoStacks`. |
| scriptedScalaTestStats     | Boolean                                        | **Optional**. If `true`, displays various statistics of tests. Defaults to `true`.                                                                                                                                              |

## Tasks

| Task              | Description                                                                                                                                                                  |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| scriptedScalatest | Executes all test configured in `scriptedScalaTestSpec`. This task must be [configured for scripted-plugin to run in the `test` script file](#step-5-configure-test-script). |
