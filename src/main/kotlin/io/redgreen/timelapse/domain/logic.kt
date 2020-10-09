package io.redgreen.timelapse.domain

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

fun getCommitHistoryText(filePath: String): String {
  return """
      77bc5c1 (HEAD -> develop) chore: update Kotlin version
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
      a3ceb58 chore: update AGP and Gradle versions
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
      208873f Bump up dependencies for `android-sdk` module
       build.gradle | 2 ++
       1 file changed, 2 insertions(+)
      b35d60f Bump up dependencies for `server` module
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
      ad2cbb7 Bump AGP version to 3.6.1
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
      9ccf747 Include an app module for writing instrumented tests
       build.gradle | 2 +-
       1 file changed, 1 insertion(+), 1 deletion(-)
      79c6217 Add an Android SDK module
       build.gradle | 14 ++++++++++++++
       1 file changed, 14 insertions(+)
      986c5ff Get rid of root project and optimize gradle buildscripts
       build.gradle | 30 ++++++++----------------------
       1 file changed, 8 insertions(+), 22 deletions(-)
      f5a1ed7 Add canary test targeting Java 9
       build.gradle | 25 +++++++++++++++++++++++++
       1 file changed, 25 insertions(+)
  """.trimIndent()
}

fun openGitRepository(repositoryPath: File): Repository {
  val gitDirectory = "${repositoryPath.canonicalPath}${File.separator}.git"

  val repository = FileRepositoryBuilder()
    .setGitDir(File(gitDirectory))
    .build()

  return if (repository.branch != null) {
    repository
  } else {
    throw IllegalStateException("Not a git directory: ${repositoryPath.canonicalPath}")
  }
}
