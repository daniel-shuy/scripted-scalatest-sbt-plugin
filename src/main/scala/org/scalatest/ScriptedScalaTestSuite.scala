package org.scalatest

import org.scalactic.Prettifier._
import org.scalactic.Requirements._
import org.scalatest.Suite.{formatterForSuiteAborted, formatterForSuiteCompleted, formatterForSuiteStarting, getTopOfClass}
import org.scalatest.events._
import org.scalatest.tools.StandardOutReporter

import scala.util.control.NonFatal

/**
  * This class has the same package as [[org.scalatest.Suite]] so that it can access protected or package private
  * members.
  */
trait ScriptedScalaTestSuite extends Suite {
  thisSuite: Suite =>
  /**
    * Copied from [[org.scalatest.Suite.execute(String,org.scalatest.ConfigMap,Boolean,Boolean,Boolean,Boolean,Boolean):Unit]],
    * but modified to return [[org.scalatest.Status]] instead of [[scala.Unit]].
    *
    * @return [[org.scalatest.FailedStatus]] on error,
    *        else the return from [[org.scalatest.Suite.run(Option[String],org.scalatest.Args)]].
    */
  final def executeScripted(testName: String = null,
                      configMap: ConfigMap = ConfigMap.empty,
                      color: Boolean = true,
                      durations: Boolean = false,
                      shortstacks: Boolean = false,
                      fullstacks: Boolean = false,
                      stats: Boolean = false
                     ): Status = {
    requireNonNull(configMap)
    val SelectedTag = "Selected"
    val SelectedSet = Set(SelectedTag)
    val desiredTests: Set[String] =
      if (testName == null) Set.empty
      else {
        testNames.filter { s =>
          s.indexOf(testName) >= 0 || NameTransformer.decode(s).indexOf(testName) >= 0
        }
      }
    if (testName != null && desiredTests.isEmpty)
      throw new IllegalArgumentException(Resources.testNotFound(testName))

    val dispatch = new DispatchReporter(List(new StandardOutReporter(durations, color, shortstacks, fullstacks, false, false, false, false, false, false)))
    val tracker = new Tracker
    val filter =
      if (testName == null) Filter()
      else {
        val taggedTests: Map[String, Set[String]] = desiredTests.map(_ -> SelectedSet).toMap
        Filter(
          tagsToInclude = Some(SelectedSet),
          excludeNestedSuites = true,
          dynaTags = DynaTags(Map.empty, Map(suiteId -> taggedTests))
        )
      }
    val runStartTime = System.currentTimeMillis
    if (stats)
      dispatch(RunStarting(tracker.nextOrdinal(), expectedTestCount(filter), configMap))

    val suiteStartTime = System.currentTimeMillis
    def dispatchSuiteAborted(e: Throwable): Unit = {
      val eMessage = e.getMessage
      val rawString =
        if (eMessage != null && eMessage.length > 0)
          Resources.runOnSuiteException
        else
          Resources.runOnSuiteExceptionWithMessage(eMessage)
      val formatter = formatterForSuiteAborted(thisSuite, rawString)
      val duration = System.currentTimeMillis - suiteStartTime
      dispatch(SuiteAborted(tracker.nextOrdinal(), rawString, thisSuite.suiteName, thisSuite.suiteId, Some(thisSuite.getClass.getName), Some(e), Some(duration), formatter, Some(SeeStackDepthException)))
    }

    try {

      val formatter = formatterForSuiteStarting(thisSuite)
      dispatch(SuiteStarting(tracker.nextOrdinal(), thisSuite.suiteName, thisSuite.suiteId, Some(thisSuite.getClass.getName), formatter, Some(getTopOfClass(thisSuite))))

      val status =
        run(
          None,
          Args(dispatch,
            Stopper.default,
            filter,
            configMap,
            None,
            tracker,
            Set.empty)
        )
      status.waitUntilCompleted()
      val suiteCompletedFormatter = formatterForSuiteCompleted(thisSuite)
      val duration = System.currentTimeMillis - suiteStartTime
      dispatch(SuiteCompleted(tracker.nextOrdinal(), thisSuite.suiteName, thisSuite.suiteId, Some(thisSuite.getClass.getName), Some(duration), suiteCompletedFormatter, Some(getTopOfClass(thisSuite))))
      if (stats) {
        val duration = System.currentTimeMillis - runStartTime
        dispatch(RunCompleted(tracker.nextOrdinal(), Some(duration)))
      }
      status
    }
    catch {
      case e: InstantiationException =>
        dispatchSuiteAborted(e)
        dispatch(RunAborted(tracker.nextOrdinal(), Resources.cannotInstantiateSuite(e.getMessage), Some(e), Some(System.currentTimeMillis - runStartTime)))
        FailedStatus
      case e: IllegalAccessException =>
        dispatchSuiteAborted(e)
        dispatch(RunAborted(tracker.nextOrdinal(), Resources.cannotInstantiateSuite(e.getMessage), Some(e), Some(System.currentTimeMillis - runStartTime)))
        FailedStatus
      case e: NoClassDefFoundError =>
        dispatchSuiteAborted(e)
        dispatch(RunAborted(tracker.nextOrdinal(), Resources.cannotLoadClass(e.getMessage), Some(e), Some(System.currentTimeMillis - runStartTime)))
        FailedStatus
      case e: Throwable =>
        dispatchSuiteAborted(e)
        dispatch(RunAborted(tracker.nextOrdinal(), Resources.bigProblems(e), Some(e), Some(System.currentTimeMillis - runStartTime)))
        if (!NonFatal(e))
          throw e
        FailedStatus
    }
    finally {
      dispatch.dispatchDisposeAndWaitUntilDone()
    }
  }
}
