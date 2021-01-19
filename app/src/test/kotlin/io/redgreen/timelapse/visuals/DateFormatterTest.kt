package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import java.time.LocalDateTime
import java.time.Month
import java.time.Month.OCTOBER
import java.time.ZoneId
import java.util.Date
import org.junit.jupiter.api.Test

class DateFormatterTest {
  @Test
  fun `it should show author and commit date and time`() {
    // given
    val authoredDate = toUtilDate(2020, OCTOBER, 20, 22, 49)
    val committedDate = toUtilDate(2020, OCTOBER, 20, 22, 55)

    // when
    val formattedDateString = getAuthoredAndCommittedText(authoredDate, committedDate)

    // then
    assertThat(formattedDateString)
      .isEqualTo("Authored on Tue, 20 Oct 2020 22:49, committed on Tue, 20 Oct 2020 22:55")
  }

  @Suppress("SameParameterValue")
  private fun toUtilDate(
    year: Int,
    month: Month,
    dayOfMonth: Int,
    hour: Int,
    minute: Int
  ): Date {
    val localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
    return Date
      .from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
  }
}
