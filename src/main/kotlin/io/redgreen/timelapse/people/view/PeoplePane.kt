package io.redgreen.timelapse.people.view

import io.reactivex.rxjava3.functions.Consumer
import io.redgreen.timelapse.vcs.Contribution
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import org.eclipse.jgit.lib.Repository
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.LazyThreadSafetyMode.NONE

class PeoplePane(private val gitRepository: Repository) : JPanel() {
  private val gitRepositoryService by lazy(NONE) { GitRepositoryService(gitRepository) }

  private val contributorsLabel = JLabel()

  init {
    add(contributorsLabel)
  }

  fun selectFileAndRevision(filePath: String, commitId: String) {
    gitRepositoryService
      .getContributions(commitId, filePath)
      .subscribe(Consumer { contributorsLabel.text = toHtml(it) })
  }

  private fun toHtml(contributions: List<Contribution>): String {
    val htmlContributions = contributions
      .joinToString("<br />") { (identity, fraction) ->
        "${identity.name} ${String.format("%.02f%%", fraction * 100)}"
      }
    return "<html>$htmlContributions</html>"
  }
}
