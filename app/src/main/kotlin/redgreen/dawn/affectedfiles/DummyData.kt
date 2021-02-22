package redgreen.dawn.affectedfiles

import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.New
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.FileCell

internal val affectedFilesViewModels = listOf(
  DirectoryCell("guava/src/com/google/common/collect/"),
  FileCell(Modified("Collections2.java", 0, 443)),
  FileCell(Modified("Multimaps.java", 4, 0)),
  FileCell(Modified("Table.java", 3, 0)),
  FileCell(Modified("ForwardingTable.java", 2, 0)),
  FileCell(Modified("HashBasedTable.java", 2, 0)),
  FileCell(Modified("ImmutableTable.java", 2, 0)),
  FileCell(Modified("Maps.java", 1, 0)),
  FileCell(Deleted("SortedMaps.java", 374)),

  DirectoryCell("guava-tests/test/com/google/common/collect/"),
  FileCell(Modified("Collections2Test.java", 0, 279)),
  FileCell(Deleted("SortedMapsTest.java", 222)),

  DirectoryCell("guava-gwt/src-super/com/google/common/collect/super/com/google/common/collect/"),
  FileCell(Modified("Multimaps.java", 4, 11)),
  FileCell(Modified("Maps.java", 1, 0)),

  DirectoryCell("guava-tests/test/com/google/common/cache/"),
  FileCell(New("CacheBuilderSpecTest.java", 533)),

  DirectoryCell("guava/src/com/google/common/cache/"),
  FileCell(Modified("CacheBuilder.java", 0, 25)),
)
