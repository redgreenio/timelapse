package io.redgreen.timelapse.people.view

import io.redgreen.timelapse.vcs.Contribution
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import org.eclipse.jgit.lib.Repository
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JLabel.RIGHT
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import kotlin.LazyThreadSafetyMode.NONE

class PeoplePane(private val gitRepository: Repository) : JPanel(GridLayout()) {
  private val gitRepositoryService by lazy(NONE) { GitRepositoryService(gitRepository) }

  private val contributorsTable = JTable()
  private val rightAlignmentCellRenderer = DefaultTableCellRenderer().apply { horizontalAlignment = RIGHT }

  init {
    add(JScrollPane(contributorsTable))
    setTitle()
  }

  fun selectFileAndRevision(filePath: String, commitId: String) {
    gitRepositoryService
      .getContributions(commitId, filePath)
      .subscribe { contributions ->
        setTitle(contributions.size)
        with(contributorsTable) {
          model = ContributionsTableModel(contributions)
          columnModel.getColumn(1).cellRenderer = rightAlignmentCellRenderer
        }
      }
  }

  private fun setTitle(peopleCount: Int = 0) {
    val title = if (peopleCount == 0) "People" else "People ($peopleCount)"
    border = BorderFactory.createTitledBorder(title)
  }

  class ContributionsTableModel(
    contributions: List<Contribution>
  ) : DefaultTableModel() {
    private val columnNames = arrayOf("Name", "Contribution %")

    init {
      val contributionRows = contributions
        .map { (identity, fraction) -> arrayOf(identity.name, String.format("%.02f", fraction * 100)) }
        .toTypedArray()
      setDataVector(contributionRows, columnNames)
    }

    override fun isCellEditable(row: Int, column: Int): Boolean =
      false
  }
}
