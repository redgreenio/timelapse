package io.redgreen.timelapse.vcs.git

import io.redgreen.timelapse.contentviewer.data.BlobDiffInformation
import io.redgreen.timelapse.domain.BlobDiff
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.fixtures.GitFixture.Companion.INVALID_COMMIT_ID
import io.redgreen.timelapse.fixtures.GitFixture.Companion.NON_EXISTENT_FILE_PATH
import io.redgreen.timelapse.fixtures.GitTestbed
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitA
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitB
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitE
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitF
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitG
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.mergeEnglishIntoSpanish
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_1_COPY_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_1_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_2_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_3_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_4_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_A_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_B_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_C_TXT
import io.redgreen.timelapse.fixtures.SimpleAndroid
import io.redgreen.timelapse.git.model.Identity
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import io.redgreen.timelapse.vcs.Contribution
import java.time.LocalDate
import java.time.Month.OCTOBER
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GitRepositoryServiceTest {
  @Nested
  inner class GetChangedFiles {
    private val gitTestbedRepository = openGitRepository(GitTestbed.path, true)
    private val repositoryService = GitRepositoryService(gitTestbedRepository)

    @Test
    fun `it should get changed files from an initial commit`() {
      // given
      val initialCommit = exhibitA // exhibit a: add three new files

      // when
      val testObserver = repositoryService.getChangedFiles(initialCommit).test()

      // then
      val changedFiles = listOf(FILE_1_TXT, FILE_2_TXT, FILE_3_TXT).map(::Addition)
      testObserver
        .assertValue(changedFiles)
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should get changed files from a non-initial commit`() {
      // given
      val nonInitialCommit = exhibitG // exhibit g: renames, deletion, addition and modification

      // when
      val testObserver = repositoryService.getChangedFiles(nonInitialCommit).test()

      // then
      val changedFiles = listOf(
        Modification(FILE_1_TXT),
        Deletion(FILE_3_TXT),
        Deletion(FILE_4_TXT),
        Addition(FILE_B_TXT),
        Addition(FILE_C_TXT),
        Rename(FILE_A_TXT, FILE_2_TXT)
      )
      testObserver
        .assertValue(changedFiles)
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return an error for an invalid commit`() {
      // given
      val invalidCommitId = INVALID_COMMIT_ID

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
    private val simpleAndroidRepository = openGitRepository(SimpleAndroid.path, true)
    private val repositoryService = GitRepositoryService(simpleAndroidRepository)
    private val commitId = "d26b2b56696e63bffa5700488dcfe0154ad8cecd"

    @Test
    fun `it should return a single contribution for a file with just one contributor`() {
      // given
      val filePath = "newbranch" // Name of a shell script file

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
      val invalidCommitId = INVALID_COMMIT_ID
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
      // when
      val testObserver = repositoryService
        .getContributions(commitId, NON_EXISTENT_FILE_PATH)
        .test()

      // then
      testObserver
        .assertError {
          it is java.lang.IllegalArgumentException
              && it.message == "Non-existent file path at $commitId: $NON_EXISTENT_FILE_PATH"
        }
        .assertNoValues()
    }
  }

  @Nested
  inner class GetCommitOnOrAfter {
    private val simpleAndroidRepository = openGitRepository(SimpleAndroid.path, true)
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
    private val gitTestbedRepository = openGitRepository(GitTestbed.path, true)
    private val repositoryService = GitRepositoryService(gitTestbedRepository)

    @Test
    fun `it should return changed file paths for initial commit`() {
      // given
      val initialCommitId = exhibitA // exhibit a: add three new files

      // when
      val testObserver = repositoryService.getChangedFilePaths(initialCommitId).test()

      // then
      testObserver
        .assertValue(
          listOf(
            FILE_1_TXT,
            FILE_2_TXT,
            FILE_3_TXT,
          )
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return changed file paths between a parent and a child`() {
      // given
      val parentCommitId = exhibitA // exhibit a: add three new files
      val childCommitId = exhibitB // exhibit b: add a new file

      // when
      val testObserver = repositoryService.getChangedFilePaths(childCommitId, parentCommitId).test()

      // then
      testObserver
        .assertValue(
          listOf(FILE_4_TXT)
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should return changed file paths between an ancestor and a descendant`() {
      // given
      val parentCommitId = exhibitA // exhibit a: add three new files
      val childCommitId = exhibitF // exhibit f: rename a file

      // when
      val testObserver = repositoryService.getChangedFilePaths(childCommitId, parentCommitId).test()

      // then
      testObserver
        .assertValue(
          listOf(
            FILE_1_TXT,
            FILE_4_TXT,
          )
        )
        .assertNoErrors()
        .assertComplete()
    }

    @Test
    fun `it should ignore deleted file paths between an ancestor and a descendant (simple-android)`() {
      // given
      val repositoryService = GitRepositoryService(openGitRepository(SimpleAndroid.path, true))
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

  @Nested
  inner class GetBlobDiff {
    private val gitTestbedRepository = openGitRepository(GitTestbed.path, true)
    private val repositoryService = GitRepositoryService(gitTestbedRepository)

    @Test
    fun `it should get the blob diff of a simple commit`() {
      // given
      val selectedFilePath = FILE_1_COPY_TXT
      val commitId = exhibitE // exhibit e: copy a file

      // when
      val testObserver = repositoryService
        .getBlobDiff(selectedFilePath, commitId)
        .test()

      // then
      val blobDiff = BlobDiff.Simple(
        "68958540148efb4dd0dbfbb181df330deaffbe13",
        """
          diff --git a/file-1-copy.txt b/file-1-copy.txt
          new file mode 100644
          index 0000000..980a0d5
          --- /dev/null
          +++ b/file-1-copy.txt
          @@ -0,0 +1 @@
          +Hello World!
          
        """.trimIndent()
      )
      testObserver
        .assertValue(blobDiff)
    }

    @Test
    fun `it should get the blob diff for a merge commit`() {
      // given
      val selectedFilePath = FILE_1_TXT
      val commitId = mergeEnglishIntoSpanish // Merge branch 'english' into spanish

      // when
      val testObserver = repositoryService
        .getBlobDiff(selectedFilePath, commitId)
        .test()

      // then
      testObserver
        .assertValue(getMergeBlobDiff())
    }

    @Test
    fun `it should return an error if the selected file path is invalid`() {
      // given
      val selectedFilePath = NON_EXISTENT_FILE_PATH
      val commitId = exhibitE // exhibit e: copy a file

      // when
      val testObserver = repositoryService
        .getBlobDiff(selectedFilePath, commitId)
        .test()

      // then
      testObserver
        .assertError {
          it is IllegalArgumentException && it.message == "File path does not exist: $selectedFilePath"
        }
    }

    @Test
    fun `it should return an error if the selected commit ID is invalid`() {
      // given
      val selectedFilePath = FILE_1_COPY_TXT
      val commitId = INVALID_COMMIT_ID

      // when
      val testObserver = repositoryService
        .getBlobDiff(selectedFilePath, commitId)
        .test()

      // then
      testObserver
        .assertError {
          it is IllegalArgumentException && it.message == "Invalid commit ID: $commitId"
        }
    }

    // FIXME: 04-12-2020 This is duplicated code. Consider moving these into a Gradle Test Fixtures project
    private fun getMergeBlobDiff(): BlobDiff.Merge {
      return BlobDiff.Merge(
        listOf(
          BlobDiff.Simple(
            "1865160d483f9b22dfa9b49d0305c167746d9f7a",
            """
            diff --git a/file-1.txt b/file-1.txt
            index 265d673..f17d600 100644
            --- a/file-1.txt
            +++ b/file-1.txt
            @@ -1 +1 @@
            -Hola, mundo!
            +Hola, world!
            
          """.trimIndent()
          ),

          BlobDiff.Simple(
            "6ad80c13f9d08fdfc1bd0ab7299a2178183326a1",
            """
            diff --git a/file-1.txt b/file-1.txt
            index af5626b..f17d600 100644
            --- a/file-1.txt
            +++ b/file-1.txt
            @@ -1 +1 @@
            -Hello, world!
            +Hola, world!
            
          """.trimIndent()
          )
        )
      )
    }
  }

  @Nested
  inner class GetBlobDiffInformation {
    private val gitTestbedRepository = openGitRepository(GitTestbed.path, true)
    private val repositoryService = GitRepositoryService(gitTestbedRepository)

    @Test
    fun `it should fetch blob diff information for a simple commit`() {
      // given
      val selectedFilePath = FILE_1_TXT
      val commitId = exhibitA // exhibit a: add three new files

      // when
      val testObserver = repositoryService
        .getBlobDiffInformation(selectedFilePath, commitId)
        .test()

      // then
      val message = "exhibit a: add three new files"
      testObserver
        .assertValue(BlobDiffInformation(selectedFilePath, commitId, message, 0, 0, 3))
    }

    @Test
    fun `it should fetch blob diff information for a simple commit with non-zero deletions and insertions`() {
      // given
      val selectedFilePath = FILE_1_TXT
      val commitId = mergeEnglishIntoSpanish // Merge branch 'english' into spanish

      // when
      val testObserver = repositoryService
        .getBlobDiffInformation(selectedFilePath, commitId)
        .test()

      // then
      val message = "Merge branch 'english' into spanish"
      testObserver
        .assertValue(BlobDiffInformation(selectedFilePath, commitId, message, 1, 1, 1))
    }

    @Test
    fun `it should return an error if the selected path is invalid`() {
      // given
      val selectedFilePath = NON_EXISTENT_FILE_PATH
      val commitId = exhibitA // exhibit a: add three new files

      // when
      val testObserver = repositoryService
        .getBlobDiffInformation(selectedFilePath, commitId)
        .test()

      // then
      testObserver
        .assertError {
          it is IllegalArgumentException && it.message == "File path does not exist: $selectedFilePath"
        }
    }

    @Test
    fun `it should return an error if the commit ID is invalid`() {
      // given
      val selectedFilePath = NON_EXISTENT_FILE_PATH
      val commitId = INVALID_COMMIT_ID

      // when
      val testObserver = repositoryService
        .getBlobDiffInformation(selectedFilePath, commitId)
        .test()

      // then
      testObserver
        .assertError {
          it is IllegalArgumentException && it.message == "Invalid descendent commit ID: $commitId"
        }
    }
  }
}
