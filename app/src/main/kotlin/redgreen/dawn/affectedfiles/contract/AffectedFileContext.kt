package redgreen.dawn.affectedfiles.contract

data class AffectedFileContext(
  val filePath: String,
  val ancestor: CommitHash,
  val descendent: CommitHash
)
