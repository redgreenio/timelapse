package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.visuals.DiffSpan.Blank
import io.redgreen.timelapse.visuals.DiffSpan.Deletion
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.DiffSpan.Unmodified
import org.junit.jupiter.api.Test

class FormattedDiffTest {
  @Test
  fun `it should create a formatted diff from raw diff`() {
    // given
    val rawDiff = """
      diff --git a/build.gradle b/build.gradle
      index 4604741..2bc696d 100644
      --- a/build.gradle
      +++ b/build.gradle
      @@ -1,6 +1,6 @@
       buildscript {
         apply from: 'buildscripts/plugins.gradle'
      -  ext.kotlin_version = '1.3.70'
      +  ext.kotlin_version = '1.3.72'
       
         repositories {
           mavenCentral()
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.spans)
      .containsExactly(
        Unmodified("buildscript {"),
        Unmodified("  apply from: 'buildscripts/plugins.gradle'"),
        Deletion("  ext.kotlin_version = '1.3.70'"),
        Insertion("  ext.kotlin_version = '1.3.72'"),
        Blank,
        Unmodified("  repositories {"),
        Unmodified("    mavenCentral()"),
      )
  }

  @Test
  fun `it should create a formatted diff for a deleted file`() {
    // given
    val rawDiff = """
      diff --git a/file-4.txt b/file-4.txt
      deleted file mode 100644
      index 980a0d5..0000000
      --- a/file-4.txt
      +++ /dev/null
      @@ -1 +0,0 @@
      -Hello World!
       
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.spans)
      .containsExactly(
        Deletion("Hello World!"),
        Blank,
      )
  }

  @Test
  fun `it should create a formatted diff for a newly added file`() {
    // given
    val rawDiff = """
      diff --git a/file-c.txt b/file-c.txt
      new file mode 100644
      index 0000000..8bc25bf
      --- /dev/null
      +++ b/file-c.txt
      @@ -0,0 +1 @@
      +I'm new in town :)
       
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.spans)
      .containsExactly(
        Insertion("I'm new in town :)"),
        Blank,
      )
  }
}
