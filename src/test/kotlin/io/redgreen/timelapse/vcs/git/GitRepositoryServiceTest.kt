package io.redgreen.timelapse.vcs.git

import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import io.redgreen.timelapse.vcs.Contribution
import io.redgreen.timelapse.vcs.Identity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.Month.OCTOBER

class GitRepositoryServiceTest {
  @Nested
  inner class GetChangedFiles {
    private val gitTestbedRepository = openGitRepository(File("git-testbed"))
    private val repositoryService = GitRepositoryService(gitTestbedRepository)

    @Test
    fun `it should get changed files from an initial commit`() {
      // given
      val initialCommit = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files

      // when
      val testObserver = repositoryService.getChangedFiles(initialCommit).test()

      // then
      val changedFiles = listOf("file-1.txt", "file-2.txt", "file-3.txt").map(::Addition)
      testObserver
        .assertValue(changedFiles)
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should get changed files from a non-initial commit`() {
      // given
      val nonInitialCommit =
        "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

      // when
      val testObserver = repositoryService.getChangedFiles(nonInitialCommit).test()

      // then
      val changedFiles = listOf(
        Modification("file-1.txt"),
        Deletion("file-3.txt"),
        Deletion("file-4.txt"),
        Addition("file-b.txt"),
        Addition("file-c.txt"),
        Rename("file-a.txt", "file-2.txt")
      )
      testObserver
        .assertValue(changedFiles)
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return an error for an invalid commit`() {
      // given
      val invalidCommitId = "invalid-commit-id"

      // when
      val testObserver = repositoryService.getChangedFiles(invalidCommitId).test()

      // then
      testObserver
        .assertNoValues()
        .assertError { throwable ->
          throwable is IllegalArgumentException && throwable.message == ("Invalid commit ID: $invalidCommitId")
        }
    }
  }

  @Nested
  inner class GetContributions {
    private val simpleAndroidRepository = openGitRepository(File("simple-android"))
    private val repositoryService = GitRepositoryService(simpleAndroidRepository)
    private val commitId = "d26b2b56696e63bffa5700488dcfe0154ad8cecd"

    @Test
    fun `it should return a single contribution for a file with just one contributor`() {
      // given
      val filePath = "newbranch"

      // when
      val testObserver = repositoryService.getContributions(commitId, filePath).test()

      // then
      testObserver
        .assertValue(listOf(Contribution(Identity("Ragunath Jawahar", "ragunath@obvious.in"), 1.0)))
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return several contributions for a file with multiple contributors`() {
      // given
      val filePath = "app/src/androidTest/java/org/simple/clinic/di/TestAppComponent.kt"

      // when
      val testObserver = repositoryService.getContributions(commitId, filePath).test()

      // then
      testObserver
        .assertValue(
          listOf(
            Contribution(Identity("Vinay Shenoy", "vinay@obvious.in"), 0.26956521739130435),
            Contribution(Identity("Vinay S Shenoy", "vinay@obvious.in"), 0.17391304347826086),
            Contribution(Identity("Pratul Kalia", "pratul@uncommon.is"), 0.10434782608695652),
            Contribution(Identity("Saket Narayan", "saket@saket.me"), 0.0782608695652174),
            Contribution(Identity("Saket Narayan", "saket@uncommon.is"), 0.0782608695652174),
            Contribution(Identity("Sasikanth Miriyampalli", "sasikanth@obvious.in"), 0.06956521739130435),
            Contribution(Identity("Rakshak R.Hegde", "rakshak@obvious.in"), 0.05217391304347826),
            Contribution(Identity("Sanchita Agarwal", "sanchita@uncommon.is"), 0.05217391304347826),
            Contribution(Identity("Honey Sonwani", "honey@obvious.in"), 0.05217391304347826),
            Contribution(Identity("Vinay S Shenoy", "vinay.sh@uncommon.is"), 0.034782608695652174),
            Contribution(Identity("Sanchita Agarwal", "sanchita@obvious.in"), 0.017391304347826087),
            Contribution(Identity("Sasikanth Miriyampalli", "sasikanthmiriyampalli@gmail.com"), 0.017391304347826087),
          )
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return an error if the commit ID is invalid`() {
      // given
      val invalidCommitId = "invalid-commit-id"
      val filePath = "app/src/androidTest/java/org/simple/clinic/di/TestAppComponent.kt"

      // when
      val testObserver = repositoryService.getContributions(invalidCommitId, filePath).test()

      // then
      testObserver
        .assertError {
          it is IllegalArgumentException
              && it.message == "Invalid commit ID: $invalidCommitId"
        }
        .assertNoValues()
    }

    @Test
    fun `it should return an error if the file path is non-existent`() {
      // given
      val nonExistentFilePath = "non/existent/file/path"

      // when
      val testObserver = repositoryService.getContributions(commitId, nonExistentFilePath).test()

      // then
      testObserver
        .assertError {
          it is java.lang.IllegalArgumentException
              && it.message == "Non-existent file path at $commitId: $nonExistentFilePath"
        }
        .assertNoValues()
    }
  }

  @Nested
  inner class GetCommitOnOrAfter {
    private val simpleAndroidRepository = openGitRepository(File("simple-android"))
    private val repositoryService = GitRepositoryService(simpleAndroidRepository)

    @Test
    fun `it should return the first commit on a given date`() {
      // given
      val october26 = LocalDate.of(2020, OCTOBER, 26)

      // when
      val testObserver = repositoryService.getFirstCommitOnOrAfter(october26).test()

      // then
      testObserver
        .assertValue("f7a3080ee72869bd9925eaef49cb0de75acc7083") // Update CHANGELOG (#2037)
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return a commit that is closest to a date if the specified date does not have any commits`() {
      // given
      val october24 = LocalDate.of(2020, OCTOBER, 24)

      // when
      val testObserver = repositoryService.getFirstCommitOnOrAfter(october24).test()

      // then
      testObserver
        .assertValue("f7a3080ee72869bd9925eaef49cb0de75acc7083") // Update CHANGELOG (#2037)
        .assertNoErrors()
        .assertComplete()
    }
  }

  @Nested
  inner class GetChangedFilePaths {
    private val gitTestbedRepository = openGitRepository(File("git-testbed"))
    private val repositoryService = GitRepositoryService(gitTestbedRepository)

    @Test
    fun `it should return changed file paths for initial commit`() {
      // given
      val initialCommitId = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files

      // when
      val testObserver = repositoryService.getChangedFilePaths(initialCommitId).test()

      // then
      testObserver
        .assertValue(
          listOf(
            "file-1.txt",
            "file-2.txt",
            "file-3.txt",
          )
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return changed file paths between a parent and a child`() {
      // given
      val parentCommitId = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files
      val childCommitId = "b0d86a6cf1f8c9a12b25f2f51f5be97b61647075" // exhibit b: add a new file

      // when
      val testObserver = repositoryService.getChangedFilePaths(childCommitId, parentCommitId).test()

      // then
      testObserver
        .assertValue(
          listOf("file-4.txt")
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return changed file paths between an ancestor and a descendant`() {
      // given
      val parentCommitId = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files
      val childCommitId = "f1027401b8d62cd699f286b8eb8e049645654909" // exhibit f: rename a file

      // when
      val testObserver = repositoryService.getChangedFilePaths(childCommitId, parentCommitId).test()

      // then
      testObserver
        .assertValue(
          listOf(
            "file-1.txt",
            "file-4.txt",
          )
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should ignore deleted file paths between an ancestor and a descendant (simple-android)`() {
      // given
      val repositoryService = GitRepositoryService(openGitRepository(File("simple-android")))
      val ancestorCommitId = "f7a3080ee72869bd9925eaef49cb0de75acc7083" // Update CHANGELOG (#2037)
      val descendantCommitId = "d26b2b56696e63bffa5700488dcfe0154ad8cecd" // Update CHANGELOG (#2043)

      // when
      val testObserver = repositoryService
        .getChangedFilePaths(descendantCommitId, ancestorCommitId)
        .test()

      // then
      testObserver
        .assertValue(
          listOf(
            "CHANGELOG.md",
            "app/build.gradle",
            "app/src/androidTest/java/org/simple/clinic/di/TestAppComponent.kt",
            "app/src/androidTest/java/org/simple/clinic/patient/PatientRepositoryAndroidTest.kt",
            "app/src/main/AndroidManifest.xml",
            "app/src/main/java/org/simple/clinic/drugs/PrescribedDrug.kt",
            "app/src/main/java/org/simple/clinic/patient/PatientModule.kt",
            "app/src/main/java/org/simple/clinic/patient/PatientRepository.kt",
            "app/src/main/java/org/simple/clinic/patient/businessid/BusinessId.kt",
            "app/src/main/java/org/simple/clinic/patient/businessid/BusinessIdMetaData.kt",
            "app/src/main/res/layout/screen_patient_summary.xml",
            "app/src/main/res/layout/screen_splash.xml",
            "app/src/sharedTest/java/org/simple/clinic/TestData.kt",
            "app/src/test/java/org/simple/clinic/patient/PatientRepositoryTest.kt",
            "build.gradle",
            "gradle/wrapper/gradle-wrapper.properties",
          )
        )
        .assertNoErrors()
        .assertComplete()
    }
  }
}
