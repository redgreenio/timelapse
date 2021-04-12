package io.redgreen.timelapse.workbench.menu

interface FileMenuItemsClickListener {
  fun onRecentRepositoryClicked(directoryPath: String)
  fun onClearRecentClicked()
  fun onCloseProjectClicked()
}
