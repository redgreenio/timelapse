package com.approvaltests.model

import com.intellij.openapi.vfs.VirtualFile

sealed class ApprovalFile {
  companion object {
    fun from(virtualFile: VirtualFile): ApprovalFile? {
      val fileName = virtualFile.name
      return when {
        fileName.contains(Slug.APPROVED.text) -> Approved(virtualFile)
        fileName.contains(Slug.RECEIVED.text) -> Received(virtualFile)
        else -> null
      }
    }
  }

  internal enum class Slug {
    RECEIVED {
      override val text: String = ".received."
      override val counterpart: Slug by lazy { valueOf("APPROVED") }
    },

    APPROVED {
      override val text: String = ".approved."
      override val counterpart: Slug by lazy { RECEIVED }
    };

    abstract val text: String
    abstract val counterpart: Slug
  }

  abstract val virtualFile: VirtualFile
  internal abstract val slug: Slug
  internal abstract val counterpartCreator: (VirtualFile) -> ApprovalFile

  fun counterpart(): ApprovalFile? {
    val counterpartFileName = virtualFile.name.replace(slug.text, slug.counterpart.text)
    val counterpartVirtualFile = virtualFile.parent.findChild(counterpartFileName) ?: return null
    return counterpartCreator(counterpartVirtualFile)
  }

  class Approved(override val virtualFile: VirtualFile) : ApprovalFile() {
    override val slug = Slug.APPROVED
    override val counterpartCreator: (VirtualFile) -> ApprovalFile = { Received(it) }
  }

  class Received(override val virtualFile: VirtualFile) : ApprovalFile() {
    override val slug: Slug = Slug.RECEIVED
    override val counterpartCreator: (VirtualFile) -> ApprovalFile = { Approved(it) }

    val approvedFileName: String
      get() = virtualFile.name.replace(slug.text, slug.counterpart.text)
  }
}
