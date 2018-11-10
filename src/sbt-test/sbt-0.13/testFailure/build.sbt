import com.github.daniel.shuy.sbt.scripted.scalatest.ScriptedScalaTestSuiteMixin
import org.scalatest.Assertions._
import org.scalatest.WordSpec

lazy val testFailure = project
  .in(file("."))
  .settings(
    name := "test/sbt-0.13/testFailure",

    ScriptedPlugin.scriptedSettings,
    sys.props.get("plugin.version") match {
      case Some(pluginVersion) => scriptedLaunchOpts := { scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + pluginVersion)
      }
      case _ => sys.error("""|The system property 'plugin.version' is not defined.
                             |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
    },
    scriptedBufferLog := false,

    scriptedScalaTestStacks := SbtScriptedScalaTest.FullStacks,
    scriptedScalaTestSpec := Some(new WordSpec with ScriptedScalaTestSuiteMixin {
      override val sbtState: State = state.value

      "scripted" should {
        "fail on ScalaTest failure" in {
          assertThrows[sbt.Incomplete](
            Project.extract(sbtState)
              .runInputTask(scripted, "", sbtState))
        }
      }
    })
  )
