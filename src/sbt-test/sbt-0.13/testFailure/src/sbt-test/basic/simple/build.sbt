import com.github.daniel.shuy.sbt.scripted.scalatest.ScriptedScalaTestSuiteMixin
import org.scalatest.Assertions._
import org.scalatest.WordSpec

lazy val testBasicSimple = project
  .in(file("."))
  .settings(
    name := "test/basic/simple",

    scriptedScalaTestStacks := SbtScriptedScalaTest.FullStacks,
    scriptedScalaTestSpec := Some(new WordSpec with ScriptedScalaTestSuiteMixin {
      override val sbtState: State = state.value

      "scripted test" should {
        "fail" in {
          fail()
        }
      }
    })
  )
