package io.redgreen.timelapse.visuals

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.util.Date

class DateFormatterTest {
  @Test
  fun `it should show author and commit date and time`() {
    // given
    val year = 2020 - 1900
    val october = 9
    val authoredDate = Date(year, october, 20, 22, 49)
    val committedDate = Date(year, october, 20, 22, 55)

    // when
    val formattedDateString = formatDate(authoredDate, committedDate)

    // then
    assertThat(formattedDateString)
      .isEqualTo("Tue, 20 Oct 2020 22:49, committed on Tue, 20 Oct 2020 22:55")
  }
}
