package io.redgreen.timelapse.dev.model

import com.intellij.openapi.vfs.VirtualFile

sealed class ApprovalFile {
  abstract val virtualFile: VirtualFile

  abstract fun counterpart(): ApprovalFile?

  companion object {
    private const val APPROVED_SLUG = ".approved."
    private const val RECEIVED_SLUG = ".received."

    fun from(virtualFile: VirtualFile): ApprovalFile? {
      return when {
        virtualFile.name.contains(APPROVED_SLUG) -> Approved(virtualFile)
        virtualFile.name.contains(RECEIVED_SLUG) -> Received(virtualFile)
        else -> null
      }
    }
  }

  data class Approved(override val virtualFile: VirtualFile) : ApprovalFile() {
    override fun counterpart(): ApprovalFile? {
      val receivedFileName = virtualFile.name.replace(APPROVED_SLUG, RECEIVED_SLUG)
      val receivedVirtualFile = virtualFile.parent.findChild(receivedFileName) ?: return null
      return Received(receivedVirtualFile)
    }
  }

  data class Received(override val virtualFile: VirtualFile) : ApprovalFile() {
    val approvedFileName: String
      get() = virtualFile.name.replace(RECEIVED_SLUG, APPROVED_SLUG)

    override fun counterpart(): ApprovalFile? {
      val approvedFileName = virtualFile.name.replace(RECEIVED_SLUG, APPROVED_SLUG)
      val approvedVirtualFile = virtualFile.parent.findChild(approvedFileName) ?: return null
      return Approved(approvedVirtualFile)
    }
  }
}
