package toys.affectedfiles.model

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init
import toys.affectedfiles.model.AffectedFilesEffect.DiscoverGitRepos

object AffectedFilesInit : Init<AffectedFilesModel, AffectedFilesEffect> {
  override fun init(
    model: AffectedFilesModel
  ): First<AffectedFilesModel, AffectedFilesEffect> {
    val directoryPath = "/Users/ragunathjawahar/GitHubProjects/"

    return first(
      AffectedFilesModel.fetchingGitRepos,
      setOf(DiscoverGitRepos(directoryPath))
    )
  }
}
