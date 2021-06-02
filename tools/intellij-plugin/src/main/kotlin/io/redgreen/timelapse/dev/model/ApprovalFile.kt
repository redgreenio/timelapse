package io.redgreen.timelapse.dev.model

import com.intellij.openapi.vfs.VirtualFile

sealed class ApprovalFile {
  abstract val virtualFile: VirtualFile

  abstract fun counterpart(): ApprovalFile?

  companion object {
    fun from(virtualFile: VirtualFile): ApprovalFile? {
      val fileName = virtualFile.name
      return when {
        fileName.contains(Approved.SLUG) -> Approved(virtualFile)
        fileName.contains(Received.SLUG) -> Received(virtualFile)
        else -> null
      }
    }
  }

  data class Approved(override val virtualFile: VirtualFile) : ApprovalFile() {
    companion object {
      internal const val SLUG = ".approved."
    }

    override fun counterpart(): ApprovalFile? {
      val receivedFileName = virtualFile.name.replace(SLUG, Received.SLUG)
      val receivedVirtualFile = virtualFile.parent.findChild(receivedFileName) ?: return null
      return Received(receivedVirtualFile)
    }
  }

  data class Received(override val virtualFile: VirtualFile) : ApprovalFile() {
    companion object {
      internal const val SLUG = ".received."
    }

    val approvedFileName: String
      get() = virtualFile.name.replace(SLUG, Approved.SLUG)

    override fun counterpart(): ApprovalFile? {
      val approvedFileName = virtualFile.name.replace(SLUG, Approved.SLUG)
      val approvedVirtualFile = virtualFile.parent.findChild(approvedFileName) ?: return null
      return Approved(approvedVirtualFile)
    }
  }
}
