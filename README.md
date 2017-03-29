# scripted-scalatest-sbt-plugin
A SBT plugin to use [ScalaTest](http://www.scalatest.org/) with scripted-plugin to test your SBT plugins

Traditionally, to test a SBT plugin, you had to create subprojects in `/sbt-test`, then in the subprojects, create SBT tasks to perform the testing, then specify the tasks to execute in a `test` file (see http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html).

This is fine when performing simple tests, but for complicated tests (see http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+6%3A+custom+assertion), this can get messy really quickly:
- It sucks to not be able to write tests in a BDD style (except by using comments, which feels clunky).
- Manually writing code to print the test results to the console for each subproject is a pain.

This plugin leverages ScalaTest's powerful assertion system (to automatically print useful messages on assertion failure) and its expressive DSLs.

This plugin allows you to use any of ScalaTest's test [Suites](http://www.scalatest.org/user_guide/selecting_a_style), including [AsyncTestSuites](http://www.scalatest.org/user_guide/async_testing).

__Warning__: Do not use ScalaTest's [ParallelTestExecution](http://doc.scalatest.org/3.0.0/index.html#org.scalatest.ParallelTestExecution) mixin with this plugin. Since the tests share the same project folder, running them in parallel may cause unpredictable issues.

## Note
When executing SBT tasks in tests, use `Project.runTask(<task>, state.value)` instead of `<task>.value`. That is because calling `<task>.value` declares it as a dependency, which executes before the tests, not when the line is called.

## Usage

### Step 1: Include the scripted-plugin in your build

See http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+2%3A+scripted-plugin

If you are using [sbt-cross-building](https://github.com/jrudolph/sbt-cross-building), don't add scripted-plugin to `project/scripted.sbt`, and replace `ScriptedPlugin.scriptedSettings` in `scripted.sbt` with `CrossBuilding.scriptedSettings`.

### Step 2: Create the test subproject

Create the test subproject in `sbt-test/<test-group>/<test-name>`.

Include your plugin in the build.

See http://www.scala-sbt.org/0.13/docs/Testing-sbt-plugins.html#step+3%3A+src%2Fsbt-test

### Step 3: Include the plugin in your build

Add the following to your `sbt-test/<test-group>/<test-name>/project/plugins.sbt`:
```scala
addSbtPlugin("com.github.daniel-shuy" % "sbt-scripted-scalatest" % "0.1.0")
```

### Step 4: Configure `test` script

Put __only__ the following in the `test` script file:

`> scripted-scalatest`

### Step 5: Configure project settings for the plugin

See [Settings](#settings).

Example:
```scala
scriptedScalaTestSpec := Some(new WordSpec with ScriptedScalaTestSuiteMixin {
  override val sbtState: State = state.value
    
  "my-task" should {
    "do something" in {
      Project.runTask(myTask, state.value)
      // ...
      // assert(...)
    }
  }
})
```

### Step 6: Use the scripted-plugin as usual

Eg. Run `sbt scripted` on the main project to execute all tests.

## Settings

| Setting                    | Type                                           | Description                                                                                                                                                                                                                     |
| -------------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| scriptedScalaTestSpec      | Option[Suite with ScriptedScalaTestSuiteMixin] | __Required__. The ScalaTest Suite/Spec. If not configured (defaults to `None`), no tests will be executed.                                                                                                                      |
| scriptedScalaTestDurations | Boolean                                        | __Optional__. If `true`, displays durations of tests. Defaults to `true`.                                                                                                                                                       |
| scriptedScalaTestStacks    | NoStacks / ShortStacks / FullStacks            | __Optional__. The length of stack traces to display for failed tests. `NoStacks` will not display any stack traces. `ShortStacks` displays short stack traces. `FullStacks` displays full stack traces. Defaults to `NoStacks`. |
| scriptedScalaTestStats     | Boolean                                        | __Optional__. If `true`, displays various statistics of tests. Defaults to `true`.                                                                                                                                              |

## Tasks

| Task               | Description                                                                                                                                                                                                                                                                 |
| ------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| scripted-scalatest | Executes all test configured in `scriptedScalaTestSpec`. This task must be [configured for scripted-plugin to run in the `test` script file](https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin/new/master?readme=1#user-content-step-4-configure-test-script). |

## Roadmap

I would like to create test cases for this plugin, ideally eventually using this plugin itself. Unfortunately, I just can't seem to figure out how to convert `scripted` from an `InputKey` into a `TaskKey` so that I can execute it with `Project.runTask`. I keep getting errors no matter what arguments I pass into `scripted.toTask`. I'll be really grateful if anyone can point me in the right direction.

When SBT 1.0.x is released, it should be possible to automatically generate the `test` script file, removing the need for [Step 4](#step-4-configure-test-script).

## Licence

Copyright 2017 Daniel Shuy

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
