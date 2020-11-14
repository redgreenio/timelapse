package io.redgreen.timelapse.diff

import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class DiffHtmlTest {
  @Test
  fun `it should create a HTML diff for diff with a single section`() {
    // given
    val diffWithSingleSection = """
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
    val diffHtml = FormattedDiff.from(diffWithSingleSection).toHtml()

    // then
    Approvals.verify(diffHtml)
  }
}
