package io.redgreen.timelapse.openrepo.storage

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import io.redgreen.timelapse.openrepo.data.RecentRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class PreferencesRecentRepositoriesStorageIntegrationTest {
  private val moshi = Moshi.Builder().build()
  private val recentRepositoriesStorage = PreferencesRecentRepositoriesStorage(moshi)

  @AfterEach
  fun teardown() {
    recentRepositoriesStorage.clearRecentRepositories()
  }

  @Test
  fun `it should return an empty list when there are no recent repositories`() {
    // when
    val recentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(recentRepositories)
      .isEmpty()
  }

  @Test
  fun `it should return a recent repository if the storage had one`() {
    // given
    val counterRepository = RecentRepository("~/FlutterProjects/counter")
    recentRepositoriesStorage.update(counterRepository)

    // when
    val recentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(recentRepositories)
      .containsExactly(counterRepository)
  }

  @Test
  fun `it should return a list of recent repositories if the storage had more than one (in LIFO order)`() {
    // given
    val recentRepositories = listOf(
      "~/FlutterProjects/counter",
      "~/GoProjects/hello-services"
    ).map(::RecentRepository)
    recentRepositories.onEach(recentRepositoriesStorage::update)

    // when
    val actualRecentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(actualRecentRepositories)
      .containsExactly(
        RecentRepository("~/GoProjects/hello-services"),
        RecentRepository("~/FlutterProjects/counter")
      )
      .inOrder()
  }
}
