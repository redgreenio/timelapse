package liftoff.affectedfiles.props.mobius

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.DiscoverGitRepos

object AffectedFilesPropsUiInit : Init<AffectedFilesPropsUiModel, AffectedFilesPropsUiEffect> {
  override fun init(
    model: AffectedFilesPropsUiModel
  ): First<AffectedFilesPropsUiModel, AffectedFilesPropsUiEffect> {
    val directoryPath = "/Users/ragunathjawahar/GitHubProjects/"

    return first(
      AffectedFilesPropsUiModel.fetchingGitRepos,
      setOf(DiscoverGitRepos(directoryPath))
    )
  }
}
