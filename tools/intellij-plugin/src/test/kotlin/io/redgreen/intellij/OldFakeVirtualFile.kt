package io.redgreen.intellij

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.InputStream
import java.io.OutputStream

class OldFakeVirtualFile constructor(
  private val name: String,
  private val otherFilesInDirectory: List<String>
) : VirtualFile() {

  @Deprecated(
    "Use the newer, well tested `FakeVirtualFile` class instead.",
    ReplaceWith("FakeVirtualFile.fileFromPath(name)")
  )
  constructor(name: String) : this(name, emptyList())

  override fun getName(): String =
    name

  override fun getFileSystem(): VirtualFileSystem {
    TODO("Not required yet :)")
  }

  override fun getPath(): String {
    TODO("Not required yet :)")
  }

  override fun isWritable(): Boolean {
    TODO("Not required yet :)")
  }

  override fun isDirectory(): Boolean {
    TODO("Not required yet :)")
  }

  override fun isValid(): Boolean {
    TODO("Not required yet :)")
  }

  override fun getParent(): VirtualFile =
    OldFakeVirtualFile("", otherFilesInDirectory)

  override fun getChildren(): Array<VirtualFile> {
    return otherFilesInDirectory
      .map { OldFakeVirtualFile(it) }
      .toTypedArray()
  }

  override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
    TODO("Not required yet :)")
  }

  override fun contentsToByteArray(): ByteArray {
    TODO("Not required yet :)")
  }

  override fun getTimeStamp(): Long {
    TODO("Not required yet :)")
  }

  override fun getLength(): Long {
    TODO("Not required yet :)")
  }

  override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
    TODO("Not required yet :)")
  }

  override fun getInputStream(): InputStream {
    TODO("Not required yet :)")
  }
}
