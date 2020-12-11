package io.redgreen.timelapse.openrepo

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.timelapse.mobius.testCase
import io.redgreen.timelapse.openrepo.view.OpenRepoView
import io.redgreen.timelapse.platform.ImmediateSchedulersProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.Optional

class OpenRepoEffectHandlerTest {
  private val gitDetector = mock<GitDetector>()
  private val view = mock<OpenRepoView>()
  private val testCase = OpenRepoEffectHandler.from(gitDetector, view, ImmediateSchedulersProvider).testCase()

  @AfterEach
  fun teardown() {
    testCase.dispose()
  }

  @Test
  fun `it should return name found if a git global username is available`() {
    // given
    val globalUsername = "Ajay"
    whenever(gitDetector.globalUsername())
      .thenReturn(Optional.of(globalUsername))

    // when
    testCase.dispatch(FindGitUsername)

    // then
    testCase
      .assertOutgoingEvents(GitUsernameFound(globalUsername))
  }

  @Test
  fun `it should return username not found if a git global username is unavailable`() {
    // given
    whenever(gitDetector.globalUsername())
      .thenReturn(Optional.empty())

    // when
    testCase.dispatch(FindGitUsername)

    // then
    testCase
      .assertOutgoingEvents(GitUsernameNotFound)
  }

  @Test
  fun `it should return username not found if an exception is thrown`() {
    // given
    whenever(gitDetector.globalUsername())
      .thenThrow(RuntimeException("Something went wrong (´・_・`)"))

    // when
    testCase.dispatch(FindGitUsername)

    // then
    testCase
      .assertOutgoingEvents(GitUsernameNotFound)
  }

  @Test
  fun `it should show a file chooser`() {
    // when
    testCase.dispatch(DisplayFileChooser)

    // then
    verify(view).displayFileChooser()
    verifyNoMoreInteractions(view)

    testCase
      .assertNoOutgoingEvents()
  }
}
