package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.visuals.DiffLine.Blank
import io.redgreen.timelapse.visuals.DiffLine.ContentsEmpty
import io.redgreen.timelapse.visuals.DiffLine.Deletion
import io.redgreen.timelapse.visuals.DiffLine.Insertion
import io.redgreen.timelapse.visuals.DiffLine.Marker
import io.redgreen.timelapse.visuals.DiffLine.Unmodified
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
        Marker("@@ -1,6 +1,6 @@"),
        Unmodified("buildscript {"),
        Unmodified("  apply from: 'buildscripts/plugins.gradle'"),
        Deletion("  ext.kotlin_version = '1.3.70'", 3),
        Insertion("  ext.kotlin_version = '1.3.72'", 3),
        Blank,
        Unmodified("  repositories {"),
        Unmodified("    mavenCentral()"),
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
        Marker("@@ -1 +0,0 @@"),
        Deletion("Hello World!", 1),
        Blank,
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
        Marker("@@ -0,0 +1 @@"),
        Insertion("I'm new in town :)", 1),
        Blank,
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
        Marker("@@ -25,6 +25,7 @@"),
        Unmodified("import retrofit.mime.FormUrlEncodedTypedOutput;"),
        Unmodified("import retrofit.mime.MultipartTypedOutput;"),
        Unmodified("import retrofit.mime.TypedOutput;"),
        Insertion("import retrofit.mime.TypedString;", 28),
        Blank,
        Unmodified("final class RequestBuilder implements RequestInterceptor.RequestFacade {"),
        Unmodified("  private final Converter converter;"),
        Marker("@@ -170,6 +171,8 @@"),
        Unmodified("          if (value != null) { // Skip null values."),
        Unmodified("            if (value instanceof TypedOutput) {"),
        Unmodified("              multipartBody.addPart(name, (TypedOutput) value);"),
        Insertion("            } else if (value instanceof String) {", 174),
        Insertion("              multipartBody.addPart(name, new TypedString((String) value));", 175),
        Unmodified("            } else {"),
        Unmodified("              multipartBody.addPart(name, converter.toBody(value));"),
        Unmodified("            }"),
      )
      .inOrder()
  }
}
