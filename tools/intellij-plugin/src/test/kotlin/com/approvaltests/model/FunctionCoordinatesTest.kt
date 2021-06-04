package com.approvaltests.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class FunctionCoordinatesTest {
  @Test
  fun `it should return best guessed approved file name`() {
    // given
    val functionCoordinates = FunctionCoordinates.from("two plus two is four", "MathTest")

    // when
    val guessedFileName = functionCoordinates.bestGuessApprovedFileName()

    // then
    assertThat(guessedFileName)
      .isEqualTo("MathTest.two plus two is four.approved.txt")
  }

  @Test
  fun `it should return best guessed received file name`() {
    // given
    val functionCoordinates = FunctionCoordinates.from("two plus two is four", "MathTest")

    // when
    val guessedFileName = functionCoordinates.bestGuessReceivedFileName()

    // then
    assertThat(guessedFileName)
      .isEqualTo("MathTest.two plus two is four.received.txt")
  }
}
