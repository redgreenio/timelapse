package io.redgreen.timelapse.git.usecases

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.fixtures.GitTestbed
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitA
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitB
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitH
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitI
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.mergeEnglishIntoSpanish
import io.redgreen.timelapse.git.model.Ancestors
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.Identity
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.Author
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.AuthoredZonedDateTime
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.CommittedZonedDateTime
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.Committer
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.Encoding
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.FullMessage
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.Parent
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.ParentCount
import io.redgreen.timelapse.git.usecases.CommitInfoUseCase.Property.ShortMessage
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.Month.OCTOBER
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

class CommitInfoUseCaseTest {
  private val useCase = CommitInfoUseCase(GitTestbed.repository)
  private val commitHash = CommitHash(exhibitB) // exhibit b: add a new file

  @ParameterizedTest
  @ArgumentsSource(PropertiesArgumentsProvider::class)
  fun `it should query individual properties`(propertyExpected: Pair<Property<Any>, Any>) {
    // given
    val (property, expected) = propertyExpected

    // when
    val actual = useCase.invoke(commitHash, property)

    // then
    assertThat(actual)
      .isEqualTo(expected)
  }

  class PropertiesArgumentsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
      return listOf(
        ShortMessage to "exhibit b: add a new file",
        Encoding to Charset.forName("UTF-8"),
        FullMessage to "exhibit b: add a new file\n",
        ParentCount to 1,
        Author to Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
        Committer to Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
        AuthoredZonedDateTime to LocalDateTime.of(2020, OCTOBER, 16, 6, 2).toUtc(),
        CommittedZonedDateTime to LocalDateTime.of(2020, OCTOBER, 16, 7, 14).toUtc(),
        Parent to Ancestors.One(CommitHash(exhibitA)), // "b6748190194e697df97d3dd9801af4f55d763ef9"
      )
        .map { Arguments.of(it) }
        .toList()
        .stream()
    }
  }

  @Test
  fun `it should return None when querying the ancestor for the root commit`() {
    // when
    val ancestors = useCase.invoke(CommitHash(exhibitA), Parent)

    // then
    assertThat(ancestors)
      .isEqualTo(Ancestors.None)
  }

  @Test
  fun `it should get more than one commit ID for a merge commit`() {
    // given
    val mergeCommitId = CommitHash(mergeEnglishIntoSpanish) // Merge branch 'english' into spanish

    // when
    val ancestors = useCase.invoke(mergeCommitId, Parent)

    // then
    val commitHashes = listOf(
      exhibitI, // exhibit i: pre-merge modification (Spanish)
      exhibitH // exhibit h: pre-merge modification (English)
    ).map(::CommitHash)
    assertThat(ancestors)
      .isEqualTo(Ancestors.Many(commitHashes))
  }

  @Test
  fun `it should query 2 properties`() {
    // when
    val shortMessageAndEncoding = useCase.invoke(commitHash, ShortMessage, Encoding, ::Pair)

    // then
    assertThat(shortMessageAndEncoding)
      .isEqualTo(Pair("exhibit b: add a new file", Charset.forName("UTF-8")))
  }

  @Test
  fun `it should query 3 properties`() {
    // when
    val authorEncodingShortMessage = useCase
      .invoke(commitHash, Author, Encoding, ShortMessage, ::Triple)

    // then
    assertThat(authorEncodingShortMessage)
      .isEqualTo(
        Triple(
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          Charset.forName("UTF-8"),
          "exhibit b: add a new file"
        )
      )
  }

  @Test
  fun `it should query 4 properties`() {
    // when
    val tuple4 = useCase.invoke(commitHash, ShortMessage, Author, AuthoredZonedDateTime, Committer, ::Tuple4)

    // then
    assertThat(tuple4)
      .isEqualTo(
        Tuple4(
          "exhibit b: add a new file",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 6, 2).toUtc(),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io")
        )
      )
  }

  @Test
  fun `it should query 5 properties`() {
    // when
    val tuple5 = useCase.invoke(
      commitHash,
      ShortMessage,
      Author,
      AuthoredZonedDateTime,
      Committer,
      CommittedZonedDateTime,
      ::Tuple5
    )

    // then
    assertThat(tuple5)
      .isEqualTo(
        Tuple5(
          "exhibit b: add a new file",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 6, 2).toUtc(),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 7, 14).toUtc()
        )
      )
  }

  @Test
  fun `it should query 6 properties`() {
    // when
    val tuple6 = useCase.invoke(
      commitHash,
      ShortMessage,
      Author,
      AuthoredZonedDateTime,
      Committer,
      CommittedZonedDateTime,
      ParentCount,
      ::Tuple6
    )

    // then
    assertThat(tuple6)
      .isEqualTo(
        Tuple6(
          "exhibit b: add a new file",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 6, 2).toUtc(),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 7, 14).toUtc(),
          1
        )
      )
  }
}

private fun LocalDateTime.toUtc(): ZonedDateTime =
  ZonedDateTime.of(this, ZoneId.of("Asia/Kolkata"))
    .withZoneSameInstant(ZoneId.of("UTC"))
