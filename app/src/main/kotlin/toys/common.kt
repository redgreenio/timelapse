package toys

internal fun getFilesInCurrentRevision(gitDirectory: String): List<String> {
  return Runtime
    .getRuntime()
    // "git ls-tree --name-only -r HEAD"
    .exec(arrayOf("git", "--git-dir", gitDirectory, "ls-tree", "--name-only", "-r", "HEAD"))
    .inputStream
    .reader()
    .use { it.readText() }
    .split("\n")
    .filter { it.isNotBlank() }
}
