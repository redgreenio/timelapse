package io.redgreen.timelapse.workbench.menu

interface FileMenuItemsClickListener {
  fun onClearRecentClicked()
  fun onRecentRepositoryClicked(directoryPath: String)
}
