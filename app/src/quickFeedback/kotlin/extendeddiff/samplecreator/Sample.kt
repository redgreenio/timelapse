package extendeddiff.samplecreator

object Samples {
  internal val EXTENDED_DIFF = Sample("extended-diff", 36)
  internal val SIMPLE_ANDROID = Sample("simple-android", 87, 29)
}

data class Sample(
  val name: String,
  val patchesCount: Int,
  val patchCountOffset: Int = 0
)
