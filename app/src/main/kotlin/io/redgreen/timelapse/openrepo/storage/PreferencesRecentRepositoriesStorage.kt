package io.redgreen.timelapse.openrepo.storage

import com.google.common.annotations.VisibleForTesting
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import java.util.prefs.Preferences

class PreferencesRecentRepositoriesStorage(
  moshi: Moshi
) : RecentRepositoriesStorage {
  companion object {
    private const val KEY_RECENT_REPOSITORIES = "recent_repositories"
    private const val DEFAULT_EMPTY_JSON = "{}"
  }

  private val recentRepositoryListAdapter = moshi.adapter<List<RecentGitRepository>>(
    Types.newParameterizedType(List::class.java, RecentGitRepository::class.java)
  )

  private val preferences = Preferences.userRoot()

  override fun update(recentGitRepository: RecentGitRepository) {
    val existingRecentRepositories = getRecentRepositories().toMutableList()
    val recentRepositoryIndex = existingRecentRepositories.indexOf(recentGitRepository)
    if (recentRepositoryIndex != -1) {
      existingRecentRepositories.removeAt(recentRepositoryIndex)
    }

    val updatedRecentRepositories = listOf(recentGitRepository) + existingRecentRepositories
    val updatedRecentRepositoriesJson = recentRepositoryListAdapter.toJson(updatedRecentRepositories)
    preferences.put(KEY_RECENT_REPOSITORIES, updatedRecentRepositoriesJson)
  }

  override fun getRecentRepositories(): List<RecentGitRepository> {
    val recentRepositoriesJson = preferences.get(KEY_RECENT_REPOSITORIES, DEFAULT_EMPTY_JSON)
    return try {
      recentRepositoryListAdapter.fromJson(recentRepositoriesJson)
    } catch (e: JsonDataException) {
      emptyList()
    } ?: emptyList()
  }

  @VisibleForTesting
  internal fun clearRecentRepositories() {
    preferences.remove(KEY_RECENT_REPOSITORIES)
  }
}
