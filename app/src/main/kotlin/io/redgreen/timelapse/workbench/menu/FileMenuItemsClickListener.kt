package io.redgreen.timelapse.workbench.menu

interface FileMenuItemsClickListener {
  fun onOpenClicked()
  fun onRecentClicked(repositoryPath: String)
  fun onClearRecentClicked()
  fun onCloseProjectClicked()
}
