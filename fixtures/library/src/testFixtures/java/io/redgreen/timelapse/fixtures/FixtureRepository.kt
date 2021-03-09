package io.redgreen.timelapse.fixtures

import java.io.File

private const val GIT_REPOS_ROOT = "../.git/modules/fixtures/"

sealed class FixtureRepository(val path: File) {
  companion object {
    const val INVALID_COMMIT_ID = "invalid-commit-id"
    const val NON_EXISTENT_FILE_PATH = "non/existent/file/path/non-existent-file.txt"
  }

  object SimpleAndroid : FixtureRepository(File("${GIT_REPOS_ROOT}simple-android"))
}

object GitTestbed : FixtureRepository(File("${GIT_REPOS_ROOT}git-testbed")) {
  object Commit {
    /** exhibit a: add three new files */
    const val exhibitA = "b6748190194e697df97d3dd9801af4f55d763ef9"

    /** exhibit b: add a new file */
    const val exhibitB = "b0d86a6cf1f8c9a12b25f2f51f5be97b61647075"

    /** exhibit c: modify a file */
    const val exhibitC = "6c2faf72204d1848bdaef44f4e69c2c4ae6ca786"

    /** exhibit d: delete a file */
    const val exhibitD = "68958540148efb4dd0dbfbb181df330deaffbe13"

    /** exhibit e: copy a file */
    const val exhibitE = "0e298ab233af0e283edff96772c75a42a21b1479"

    /** exhibit f: rename a file */
    const val exhibitF = "f1027401b8d62cd699f286b8eb8e049645654909"

    /** exhibit g: renames, deletion, addition and modification */
    const val exhibitG = "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f"

    /** exhibit h: pre-merge modification (English) */
    const val exhibitH = "6ad80c13f9d08fdfc1bd0ab7299a2178183326a1"

    /** exhibit i: pre-merge modification (Spanish) */
    const val exhibitI = "1865160d483f9b22dfa9b49d0305c167746d9f7a"

    /** Merge branch 'english' into spanish */
    const val mergeEnglishIntoSpanish = "2c132dd9e3e32b6493e7d8c8ad595ea40b54a278"
  }

  object Content {
    const val FILE_1_TXT = "file-1.txt"
    const val FILE_1_COPY_TXT = "file-1-copy.txt"
    const val FILE_2_TXT = "file-2.txt"
    const val FILE_3_TXT = "file-3.txt"
    const val FILE_4_TXT = "file-4.txt"
    const val FILE_A_TXT = "file-a.txt"
    const val FILE_B_TXT = "file-b.txt"
    const val FILE_C_TXT = "file-c.txt"
  }
}
