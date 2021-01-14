package io.redgreen.timelapse.git

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.git.CommitInfoUseCase.Property
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.Author
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.AuthoredLocalDateTime
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.CommittedLocalDateTime
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.Committer
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.Encoding
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.FullMessage
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.Parent
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.ParentCount
import io.redgreen.timelapse.git.CommitInfoUseCase.Property.ShortMessage
import io.redgreen.timelapse.vcs.Identity
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.Month.OCTOBER
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

class CommitInfoUseCaseTest {
  private val repository = openGitRepository(File("../git-testbed"))
  private val useCase = CommitInfoUseCase(repository)
  private val commitId = "b0d86a6cf1f8c9a12b25f2f51f5be97b61647075" // exhibit b: add a new file

  @ParameterizedTest
  @ArgumentsSource(PropertiesArgumentsProvider::class)
  fun `it should query individual properties`(propertyExpected: Pair<Property<Any>, Any>) {
    // given
    val (property, expected) = propertyExpected

    // when
    val actual = useCase.invoke(commitId, property)

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
        AuthoredLocalDateTime to LocalDateTime.of(2020, OCTOBER, 16, 6, 2),
        CommittedLocalDateTime to LocalDateTime.of(2020, OCTOBER, 16, 7, 14),
        Parent to Ancestors.One("b6748190194e697df97d3dd9801af4f55d763ef9"),
      )
        .map { Arguments.of(it) }
        .toList()
        .stream()
    }
  }

  @Test
  fun `it should get more than one commit ID for a merge commit`() {
    // given
    val mergeCommitId = "2c132dd9e3e32b6493e7d8c8ad595ea40b54a278" // Merge branch 'english' into spanish

    // when
    val ancestors = useCase.invoke(mergeCommitId, Parent)

    // then
    assertThat(ancestors)
      .isEqualTo(
        Ancestors.Many(
          listOf(
            "1865160d483f9b22dfa9b49d0305c167746d9f7a",
            "6ad80c13f9d08fdfc1bd0ab7299a2178183326a1"
          )
        )
      )
  }

  @Test
  fun `it should query 2 properties`() {
    // when
    val shortMessageAndEncoding = useCase.invoke(commitId, ShortMessage, Encoding, ::Pair)

    // then
    assertThat(shortMessageAndEncoding)
      .isEqualTo(Pair("exhibit b: add a new file", Charset.forName("UTF-8")))
  }

  @Test
  fun `it should query 3 properties`() {
    // when
    val authorEncodingShortMessage = useCase
      .invoke(commitId, Author, Encoding, ShortMessage, ::Triple)

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
    val tuple4 = useCase.invoke(commitId, ShortMessage, Author, AuthoredLocalDateTime, Committer, ::Tuple4)

    // then
    assertThat(tuple4)
      .isEqualTo(
        Tuple4(
          "exhibit b: add a new file",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 6, 2),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io")
        )
      )
  }

  @Test
  fun `it should query 5 properties`() {
    // when
    val tuple5 = useCase.invoke(
      commitId,
      ShortMessage,
      Author,
      AuthoredLocalDateTime,
      Committer,
      CommittedLocalDateTime,
      ::Tuple5
    )

    // then
    assertThat(tuple5)
      .isEqualTo(
        Tuple5(
          "exhibit b: add a new file",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 6, 2),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 7, 14)
        )
      )
  }

  @Test
  fun `it should query 6 properties`() {
    // when
    val tuple6 = useCase.invoke(
      commitId,
      ShortMessage,
      Author,
      AuthoredLocalDateTime,
      Committer,
      CommittedLocalDateTime,
      ParentCount,
      ::Tuple6
    )

    // then
    assertThat(tuple6)
      .isEqualTo(
        Tuple6(
          "exhibit b: add a new file",
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 6, 2),
          Identity("Ragunath Jawahar", "ragunath@redgreen.io"),
          LocalDateTime.of(2020, OCTOBER, 16, 7, 14),
          1
        )
      )
  }
}
