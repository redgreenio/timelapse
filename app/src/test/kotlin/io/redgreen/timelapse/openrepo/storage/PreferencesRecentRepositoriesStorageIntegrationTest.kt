package io.redgreen.timelapse.openrepo.storage

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import io.redgreen.timelapse.openrepo.data.RecentRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PreferencesRecentRepositoriesStorageIntegrationTest {
  private val moshi = Moshi.Builder().build()
  private val recentRepositoriesStorage = PreferencesRecentRepositoriesStorage(moshi)

  // Repository paths
  private val counter = "~/FlutterProjects/counter"
  private val helloServices = "~/GoProjects/hello-services"

  @BeforeEach
  fun setup() {
    recentRepositoriesStorage.clearRecentRepositories()
  }

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
    val counterRepository = RecentRepository(counter)
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
    val recentRepositories = listOf(counter, helloServices).map(::RecentRepository)
    recentRepositories.onEach(recentRepositoriesStorage::update)

    // when
    val actualRecentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(actualRecentRepositories)
      .containsExactly(
        RecentRepository(helloServices),
        RecentRepository(counter)
      )
      .inOrder()
  }

  @Test
  fun `it should move the recently opened repository to the top of the list`() {
    // given
    val catchUp = "~/AndroidStudioProjects/CatchUp"
    val recentRepositories = listOf(counter, helloServices, catchUp).reversed().map(::RecentRepository)
    recentRepositories.onEach(recentRepositoriesStorage::update)

    recentRepositoriesStorage.update(RecentRepository(catchUp))

    // when
    val actualRecentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(actualRecentRepositories)
      .containsExactly(
        RecentRepository(catchUp),
        RecentRepository(counter),
        RecentRepository(helloServices)
      )
      .inOrder()
  }
}
