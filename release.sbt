import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("^ test"),
  releaseStepCommandAndRemaining("^ publishLocal"),
  releaseStepCommandAndRemaining("^ scripted"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("^ publish"),
  releaseStepTask(bintrayRelease),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
