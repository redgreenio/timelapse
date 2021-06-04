package io.redgreen.intellij

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.InputStream
import java.io.OutputStream

class FakeVirtualFile private constructor(
  private val path: String,
  private val isDirectory: Boolean = false
) : VirtualFile() {
  companion object {
    private const val FILE_SEPARATOR = "/"

    fun fileFromPath(filePath: String): FakeVirtualFile {
      return FakeVirtualFile(filePath)
    }

    fun directoryFromPath(directoryPath: String): FakeVirtualFile {
      return FakeVirtualFile(directoryPath, true)
    }
  }

  override fun getName(): String {
    return path.split(FILE_SEPARATOR).last()
  }

  override fun getFileSystem(): VirtualFileSystem {
    TODO("Not yet implemented")
  }

  override fun getPath(): String {
    return path
  }

  override fun isWritable(): Boolean {
    TODO("Not yet implemented")
  }

  override fun isDirectory(): Boolean {
    return isDirectory
  }

  override fun isValid(): Boolean {
    TODO("Not yet implemented")
  }

  override fun getParent(): VirtualFile? {
    val pathSegments = path.split(FILE_SEPARATOR)
    return if (pathSegments.size == 1) {
      null
    } else {
      directoryFromPath(pathSegments.dropLast(1).joinToString(FILE_SEPARATOR))
    }
  }

  override fun getChildren(): Array<VirtualFile> {
    TODO("Not yet implemented")
  }

  override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
    TODO("Not yet implemented")
  }

  override fun contentsToByteArray(): ByteArray {
    TODO("Not yet implemented")
  }

  override fun getTimeStamp(): Long {
    TODO("Not yet implemented")
  }

  override fun getLength(): Long {
    TODO("Not yet implemented")
  }

  override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
    TODO("Not yet implemented")
  }

  override fun getInputStream(): InputStream {
    TODO("Not yet implemented")
  }
}
