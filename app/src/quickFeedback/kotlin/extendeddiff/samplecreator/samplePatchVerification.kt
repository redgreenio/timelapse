package extendeddiff.samplecreator

import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiffEngine

fun main() {
  val sample = Samples.EXTENDED_DIFF
  val seedText = readResourceFile("/samples/${sample.name}/seed.txt")

  val diffEngine = ExtendedDiffEngine.newInstance(seedText, KotlinFunctionScanner)
  var finalPatchedText = seedText
  for (i in 1..sample.patchesCount) {
    val patchFileName = "${String.format("%02d", i)}.patch"
    val patch = readResourceFile("/samples/${sample.name}/$patchFileName")
    val extendedDiff = diffEngine.extendedDiff(patch)
    if (extendedDiff is HasChanges) {
      finalPatchedText = extendedDiff.sourceCode
    } else {
      println("Oh oh! $patchFileName")
      break
    }
  }
  println(finalPatchedText)
}

private fun readResourceFile(resourcePath: String): String {
  return Sample::class.java
    .getResourceAsStream(resourcePath)
    .reader()
    .readText()
}
