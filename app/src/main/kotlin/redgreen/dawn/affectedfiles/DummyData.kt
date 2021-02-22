package redgreen.dawn.affectedfiles

import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New

internal val affectedFiles = listOf(
  New("guava/src/com/google/common/collect/FluentIterable.java", 12),
  New("guava/src/com/google/common/collect/ImmutableSet.java", 24),
  New("guava/src/com/google/common/collect/ImmutableBiMap.java", 72),
  Modified("guava/src/com/google/common/collect/ImmutableTable.java", 2, 2),
  Modified("guava/src/com/google/common/collect/LinkedHashMultimap.java", 4, 4),
  Modified("guava/src/com/google/common/collect/MapMarkerInternalMap.java", 22, 2),
  Modified("guava/src/com/google/common/collect/Maps.java", 3, 2),
  Modified("guava/src/com/google/common/collect/Lists.java", 5, 1),
  Modified("guava/src/com/google/common/collect/Tables.java", 3, 0),
  Modified("guava/src/com/google/common/collect/Trees.java", 31, 12),
  Moved("guava/src/com/google/common/collect/TopKSelector.java", 3, 3),
  Deleted("guava/src/com/google/common/collect/TreeMultiset.java", 32),
  Deleted("guava/src/com/google/common/collect/RadixTree.java", 98),
)
