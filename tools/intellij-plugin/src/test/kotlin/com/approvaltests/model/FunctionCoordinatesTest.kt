package com.approvaltests.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FunctionCoordinatesTest {
  @Nested
  inner class Approved {
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
  }

  @Nested
  inner class Received {
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
}
