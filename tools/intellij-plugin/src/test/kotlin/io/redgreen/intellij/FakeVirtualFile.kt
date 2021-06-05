package io.redgreen.intellij

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.InputStream
import java.io.OutputStream

class FakeVirtualFile private constructor(
  private val path: String,
  private val isDirectory: Boolean = false,
  private val children: List<FakeVirtualFile> = emptyList(),
  private var parent: FakeVirtualFile? = null
) : VirtualFile() {
  companion object {
    private const val FILE_SEPARATOR = "/"

    fun fileFromPath(filePath: String): FakeVirtualFile {
      return FakeVirtualFile(filePath)
    }

    fun directoryFromPath(directoryPath: String): FakeVirtualFile {
      return FakeVirtualFile(directoryPath, true)
    }

    fun directoryWithFiles(files: List<FakeVirtualFile>): VirtualFile {
      val directoryPath = getParentPath(files.first())
      val directory = FakeVirtualFile(directoryPath, true, files)
      files.onEach { it.parent = directory }
      return directory
    }

    private fun getParentPath(virtualFile: FakeVirtualFile): String {
      val possiblyNonRootPath = virtualFile.path.split(FILE_SEPARATOR).dropLast(1).joinToString(FILE_SEPARATOR)
      return possiblyNonRootPath.ifEmpty { FILE_SEPARATOR }
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
    if (parent != null) {
      return parent
    }

    val pathSegments = path.split(FILE_SEPARATOR)
    return if (pathSegments.size == 1) {
      null
    } else {
      directoryFromPath(pathSegments.dropLast(1).joinToString(FILE_SEPARATOR))
    }
  }

  override fun getChildren(): Array<VirtualFile> {
    return children.toTypedArray()
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
