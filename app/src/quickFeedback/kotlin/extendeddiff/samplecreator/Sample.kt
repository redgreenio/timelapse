package extendeddiff.samplecreator

object Samples {
  internal val EXTENDED_DIFF = Sample("extended-diff", 36)
}

data class Sample(
  val name: String,
  val patchesCount: Int
)
