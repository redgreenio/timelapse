# Changelog
## [Unreleased]

## [0.1.1] - 2020-10-18
### Added
- Show and view list of related files (and their diff) changes along with the selected file.
- Maximize window on app start.

### Changed
- Move the area chart and slider into the center panel.
- The slider thumb starts at the oldest commit instead of the latest.
- Use JetBrains Mono for code font.

### Removed
- Stop the (not so useful yet) area chart anchor from following the mouse pointer.

### Fixed
- Slider getting stuck due to missing commit IDs on certain files (spoiler - merge commits in file history).
- Selecting a file in the file explorer now works all the time!
- Fix throwing exceptions on selecting the root node from file explorer.

## [0.1.0] - 2020-10-14
### Added
- Crude UI with a file explorer, insertions area chart, code area and a slider.
- Show commit ID, message and author name in description area.
