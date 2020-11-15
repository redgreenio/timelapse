package io.redgreen.timelapse.diff

import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class DiffHtmlTest {
  @Test
  fun `single diff section`() {
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

  @Test
  fun `diff empty file`() {
    // given
    val newEmptyFileDiff = """
      diff --git a/file-b.txt b/file-b.txt
      new file mode 100644
      index 0000000..e69de29
      --- /dev/null
      +++ b/file-b.txt
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(newEmptyFileDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }

  @Test
  fun `escape HTML characters`() {
    // given
    val rawXmlDiff = """
      diff --git a/app/src/main/res/drawable/background_facility_progress_shimmer.xml b/app/src/main/res/drawable/background_facility_progress_shimmer.xml
      new file mode 100644
      index 0000000..0a87cfd
      --- /dev/null
      +++ b/app/src/main/res/drawable/background_facility_progress_shimmer.xml
      @@ -0,0 +1,5 @@
      +<?xml version="1.0" encoding="utf-8"?>
      +<shape xmlns:android="http://schemas.android.com/apk/res/android">
      +  <corners android:radius="2dp" />
      +  <solid android:color="#EBEFF0" />
      +</shape>
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(rawXmlDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }

  @Test
  fun `insertion padding space`() {
    // given
    val insertionRawDiff = """
      diff --git a/.codeclimate.yml b/.codeclimate.yml
      index 4a85e39..62c22dc 100644
      --- a/.codeclimate.yml
      +++ b/.codeclimate.yml
      @@ -46,3 +46,4 @@
         - "**/src/main/java/org/simple/clinic/storage/Migration_*.kt"
         - "**/build/"
         - "**/src/main/java/org/simple/clinic/widgets/OmegaCenterIconButton.java"
      +  - "router/"
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(insertionRawDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }
}
