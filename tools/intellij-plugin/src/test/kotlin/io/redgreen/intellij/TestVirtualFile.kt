package io.redgreen.intellij

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.InputStream
import java.io.OutputStream

class TestVirtualFile(private val name: String) : VirtualFile() {
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

  override fun getParent(): VirtualFile {
    TODO("Not required yet :)")
  }

  override fun getChildren(): Array<VirtualFile> {
    TODO("Not required yet :)")
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
