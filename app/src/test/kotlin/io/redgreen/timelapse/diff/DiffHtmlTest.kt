package io.redgreen.timelapse.diff

import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

// FIXME: 19-11-2020 Approved files are too large and styling changes affect all tests,
// consider tests that separate structure and style.
class DiffHtmlTest {
  @Test
  fun `single diff section`() {
    // given
    val singleSelectionDiff = """
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
    val diffHtml = FormattedDiff.from(singleSelectionDiff).toHtml()

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

  @Test
  fun `deleted file diff`() {
    // given
    val deletedFileRawDiff = """
      diff --git a/app/src/main/res/values/omegacenterbutton.xml b/app/src/main/res/values/omegacenterbutton.xml
      deleted file mode 100644
      index 3754d57..0000000
      --- a/app/src/main/res/values/omegacenterbutton.xml
      +++ /dev/null
      @@ -1,11 +0,0 @@
      -<?xml version="1.0" encoding="utf-8"?>
      -<resources>
      -
      -  <declare-styleable name="OmegaCenterIconButton">
      -    <attr name="drawableTint" format="color"/>
      -    <attr name="android:drawablePadding"/>
      -  </declare-styleable>
      -
      -  <dimen name="omega_default_drawable_padding">4dp</dimen>
      -
      -</resources>
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(deletedFileRawDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }

  @Test
  fun `multiple diff sections`() {
    // given
    val multipleSectionsDiff = """
      diff --git a/app/build.gradle b/app/build.gradle
      index 6c5c13b..20bdbd6 100644
      --- a/app/build.gradle
      +++ b/app/build.gradle
      @@ -1,6 +1,7 @@
       apply plugin: 'com.android.application'
       apply plugin: 'kotlin-android'
       apply plugin: 'kotlin-android-extensions'
      +apply plugin: 'kotlin-kapt'
       
       android {
         compileSdkVersion versions.compileSdk
      @@ -28,4 +29,6 @@
         implementation "com.android.support:recyclerview-v7:${'$'}versions.supportLib"
       
         implementation "com.jakewharton.timber:timber:${'$'}versions.timber"
      +  implementation "com.google.dagger:dagger:${'$'}versions.dagger"
      +  kapt "com.google.dagger:dagger-compiler:${'$'}versions.dagger"
       }
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(multipleSectionsDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }

  @Test
  fun `binary files differ`() {
    // given
    val binaryFileDiff = """
      diff --git a/gradle/wrapper/gradle-wrapper.jar b/gradle/wrapper/gradle-wrapper.jar
      new file mode 100644
      index 0000000..7a3265e
      --- /dev/null
      +++ b/gradle/wrapper/gradle-wrapper.jar
      Binary files differ
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(binaryFileDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }

  @Test
  fun `file mode changed`() {
    // given
    val fileModeChangedDiff = """
      diff --git a/.gitignore b/.gitignore
      old mode 100755
      new mode 100644
      
    """.trimIndent()

    // when
    val diffHtml = FormattedDiff.from(fileModeChangedDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }

  @Test
  fun `replace tabs with 4 spaces`() {
    // given
    val tabsDiff = """
      diff --git a/org.eclipse.jgit.ant.test/src/org/eclipse/jgit/ant/tasks/GitCloneTaskTest.java b/org.eclipse.jgit.ant.test/src/org/eclipse/jgit/ant/tasks/GitCloneTaskTest.java
      index 1d7187a..9f9d459 100644
      --- a/org.eclipse.jgit.ant.test/src/org/eclipse/jgit/ant/tasks/GitCloneTaskTest.java
      +++ b/org.eclipse.jgit.ant.test/src/org/eclipse/jgit/ant/tasks/GitCloneTaskTest.java
      @@ -66,7 +66,7 @@
       	@Before
       	public void before() throws IOException {
       		dest = createTempFile();
      -		FS.getFsTimerResolution(dest.toPath().getParent());
      +		FS.getFileStoreAttributeCache(dest.toPath().getParent());
       		project = new Project();
       		project.init();
       		enableLogging();
      
    """.trimIndent()

    // then
    val diffHtml = FormattedDiff.from(tabsDiff).toHtml()

    // then
    Approvals.verify(diffHtml)
  }
}
