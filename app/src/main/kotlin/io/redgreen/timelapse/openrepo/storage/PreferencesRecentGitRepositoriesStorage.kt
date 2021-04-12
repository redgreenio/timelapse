package io.redgreen.timelapse.openrepo.storage

import com.google.common.annotations.VisibleForTesting
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.redgreen.timelapse.do_not_rename.UserSettingsNode
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.router.Destination
import io.redgreen.timelapse.router.Destination.WELCOME_SCREEN
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.prefs.Preferences
import kotlin.reflect.KClass

class PreferencesRecentGitRepositoriesStorage(
  moshi: Moshi = Moshi.Builder().build(),
  preferencesNodeClass: KClass<*> = UserSettingsNode::class
) : RecentGitRepositoriesStorage {
  companion object {
    private const val KEY_RECENT_REPOSITORIES = "recent_repositories"
    private const val KEY_SESSION_EXIT_DESTINATION = "session_exit_destination"
    private const val DEFAULT_EMPTY_JSON = "[]"
  }

  private val logger by fastLazy {
    LoggerFactory.getLogger(PreferencesRecentGitRepositoriesStorage::class.java)
  }

  private val recentRepositoryListAdapter = moshi.adapter<List<RecentGitRepository>>(
    Types.newParameterizedType(List::class.java, RecentGitRepository::class.java)
  )

  private val preferences = Preferences.userNodeForPackage(preferencesNodeClass.java)

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
      logger.error("Unexpected preferences JSON format {}", recentRepositoriesJson, e)
      emptyList()
    } ?: emptyList()
  }

  override fun getLastOpenedRepository(): Optional<RecentGitRepository> {
    return Optional.ofNullable(getRecentRepositories().firstOrNull())
  }

  override fun setSessionExitDestination(destination: Destination) {
    preferences.put(KEY_SESSION_EXIT_DESTINATION, destination.name)
  }

  override fun getSessionExitDestination(): Destination {
    val destinationString = preferences.get(KEY_SESSION_EXIT_DESTINATION, WELCOME_SCREEN.name)
    return Destination.valueOf(destinationString)
  }

  public fun clearRecentRepositories() {
    preferences.remove(KEY_RECENT_REPOSITORIES)
  }

  @VisibleForTesting
  internal fun clearSessionExitDestination() {
    preferences.remove(KEY_SESSION_EXIT_DESTINATION)
  }
}
