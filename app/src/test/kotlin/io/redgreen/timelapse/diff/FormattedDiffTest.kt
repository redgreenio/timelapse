package io.redgreen.timelapse.diff

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.diff.DiffLine.ContentsEmpty
import io.redgreen.timelapse.diff.DiffLine.Deletion
import io.redgreen.timelapse.diff.DiffLine.FileModeChanged
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.DiffLine.Marker
import io.redgreen.timelapse.diff.DiffLine.Unmodified
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
    assertThat(formattedDiff.lines)
      .containsExactly(
        Marker.Text("@@ -1,6 +1,6 @@"),
        Unmodified("buildscript {", 1, 1),
        Unmodified("  apply from: 'buildscripts/plugins.gradle'", 2, 2),
        Deletion("  ext.kotlin_version = '1.3.70'", 3),
        Insertion("  ext.kotlin_version = '1.3.72'", 3),
        Unmodified("", 4, 4),
        Unmodified("  repositories {", 5, 5),
        Unmodified("    mavenCentral()", 6, 6),
      )
      .inOrder()
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
    assertThat(formattedDiff.lines)
      .containsExactly(
        Marker.Text("@@ -1 +0,0 @@"),
        Deletion("Hello World!", 1),
        Unmodified("", 2, 0),
      )
      .inOrder()
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
    assertThat(formattedDiff.lines)
      .containsExactly(
        Marker.Text("@@ -0,0 +1 @@"),
        Insertion("I'm new in town :)", 1),
        Unmodified("", 0, 2),
      )
      .inOrder()
  }

  @Test
  fun `it should create a diff for a deleted empty file`() {
    // given
    val rawDiff = """
      diff --git a/file-3.txt b/file-3.txt
      deleted file mode 100644
      index e69de29..0000000
      --- a/file-3.txt
      +++ /dev/null
      
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.lines)
      .containsExactly(
        ContentsEmpty,
      )
  }

  @Test
  fun `it should create a diff for a new empty file`() {
    // given
    val rawDiff = """
      diff --git a/file-b.txt b/file-b.txt
      new file mode 100644
      index 0000000..e69de29
      --- /dev/null
      +++ b/file-b.txt
      
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.lines)
      .containsExactly(
        ContentsEmpty
      )
      .inOrder()
  }

  @Test
  fun `it should create a formatted diff with multiple sections`() {
    // given
    val rawDiff = """
      diff --git a/retrofit/src/main/java/retrofit/RequestBuilder.java b/retrofit/src/main/java/retrofit/RequestBuilder.java
      index 0b85c43..e49ded2 100644
      --- a/retrofit/src/main/java/retrofit/RequestBuilder.java
      +++ b/retrofit/src/main/java/retrofit/RequestBuilder.java
      @@ -25,6 +25,7 @@
       import retrofit.mime.FormUrlEncodedTypedOutput;
       import retrofit.mime.MultipartTypedOutput;
       import retrofit.mime.TypedOutput;
      +import retrofit.mime.TypedString;
       
       final class RequestBuilder implements RequestInterceptor.RequestFacade {
         private final Converter converter;
      @@ -170,6 +171,8 @@
                 if (value != null) { // Skip null values.
                   if (value instanceof TypedOutput) {
                     multipartBody.addPart(name, (TypedOutput) value);
      +            } else if (value instanceof String) {
      +              multipartBody.addPart(name, new TypedString((String) value));
                   } else {
                     multipartBody.addPart(name, converter.toBody(value));
                   }
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawDiff)

    // then
    assertThat(formattedDiff.lines)
      .containsExactly(
        Marker.Text("@@ -25,6 +25,7 @@"),
        Unmodified("import retrofit.mime.FormUrlEncodedTypedOutput;", 25, 25),
        Unmodified("import retrofit.mime.MultipartTypedOutput;", 26, 26),
        Unmodified("import retrofit.mime.TypedOutput;", 27, 27),
        Insertion("import retrofit.mime.TypedString;", 28),
        Unmodified("", 28, 29),
        Unmodified("final class RequestBuilder implements RequestInterceptor.RequestFacade {", 29, 30),
        Unmodified("  private final Converter converter;", 30, 31),
        Marker.Text("@@ -170,6 +171,8 @@"),
        Unmodified("          if (value != null) { // Skip null values.", 170, 171),
        Unmodified("            if (value instanceof TypedOutput) {", 171, 172),
        Unmodified("              multipartBody.addPart(name, (TypedOutput) value);", 172, 173),
        Insertion("            } else if (value instanceof String) {", 174),
        Insertion("              multipartBody.addPart(name, new TypedString((String) value));", 175),
        Unmodified("            } else {", 173, 176),
        Unmodified("              multipartBody.addPart(name, converter.toBody(value));", 174, 177),
        Unmodified("            }", 175, 178),
      )
      .inOrder()
  }

  @Test
  fun `it should understand binary file diffs`() {
    // given
    val rawBinaryFileDiff = """
      diff --git a/gradle/wrapper/gradle-wrapper.jar b/gradle/wrapper/gradle-wrapper.jar
      new file mode 100644
      index 0000000..7a3265e
      --- /dev/null
      +++ b/gradle/wrapper/gradle-wrapper.jar
      Binary files differ
      
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(rawBinaryFileDiff)

    // then
    assertThat(formattedDiff.lines)
      .containsExactly(
        Marker.Binary("Binary files differ")
      )
  }

  @Test
  fun `it should understand file mode changes`() {
    // given
    val fileModeChangedDiff = """
      diff --git a/.gitignore b/.gitignore
      old mode 100755
      new mode 100644
      
    """.trimIndent()

    // when
    val formattedDiff = FormattedDiff.from(fileModeChangedDiff)

    // then
    assertThat(formattedDiff.lines)
      .containsExactly(
        FileModeChanged(100755, 100644)
      )
  }
}
