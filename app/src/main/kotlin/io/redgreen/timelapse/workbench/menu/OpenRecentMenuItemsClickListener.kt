package io.redgreen.timelapse.workbench.menu

interface OpenRecentMenuItemsClickListener {
  fun onClearRecentClicked()
  fun onRecentRepositoryClicked(directoryPath: String)
}
