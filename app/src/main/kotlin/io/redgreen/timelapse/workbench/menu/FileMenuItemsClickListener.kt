package io.redgreen.timelapse.workbench.menu

interface FileMenuItemsClickListener {
  fun onRecentClicked(repositoryPath: String)
  fun onClearRecentClicked()
  fun onCloseProjectClicked()
}
