package io.redgreen.timelapse.openrepo.storage

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PreferencesRecentGitRepositoriesStorageIntegrationTest {
  private val moshi = Moshi.Builder().build()
  private val recentRepositoriesStorage = PreferencesRecentGitRepositoriesStorage(moshi, TestUserSettingsNode::class)

  /* Used to keep test data separate from production data :) */
  private class TestUserSettingsNode

  // Repository paths
  private val counter = "~/FlutterProjects/counter/.git"
  private val helloServices = "~/GoProjects/hello-services/.git"

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
    val counterRepository = RecentGitRepository(counter)
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
    val recentRepositories = listOf(counter, helloServices).map(::RecentGitRepository)
    recentRepositories.onEach(recentRepositoriesStorage::update)

    // when
    val actualRecentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(actualRecentRepositories)
      .containsExactly(
        RecentGitRepository(helloServices),
        RecentGitRepository(counter)
      )
      .inOrder()
  }

  @Test
  fun `it should move the recently opened repository to the top of the list`() {
    // given
    val catchUp = "~/AndroidStudioProjects/CatchUp/.git"
    val recentRepositories = listOf(counter, helloServices, catchUp).reversed().map(::RecentGitRepository)
    recentRepositories.onEach(recentRepositoriesStorage::update)

    recentRepositoriesStorage.update(RecentGitRepository(catchUp))

    // when
    val actualRecentRepositories = recentRepositoriesStorage.getRecentRepositories()

    // then
    assertThat(actualRecentRepositories)
      .containsExactly(
        RecentGitRepository(catchUp),
        RecentGitRepository(counter),
        RecentGitRepository(helloServices)
      )
      .inOrder()
  }

  @Test
  fun `it should give us the latest projects from the recent list`() {
    // given
    val catchUp = "~/AndroidStudioProjects/CatchUp/.git"
    recentRepositoriesStorage.update(RecentGitRepository(catchUp))

    // when
    val lastOpenedRepository = recentRepositoriesStorage.getLastOpenedRepository()

    // then
    assertThat(lastOpenedRepository)
      .isEqualTo(RecentGitRepository(catchUp))
  }
}
