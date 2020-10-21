# Changelog
## [Unreleased]
## [0.1.2] - 2020-10-20
### Added
- Show file name on top of code panels.
- Use the 'Esc' key to dismiss the code overlay window.
- Use the 'Esc' key to bring back focus on the timelapse slider from any panel.
- Use 'Alt+1', 'Alt+2', 'Alt+3' to navigate between File explorer, reading area, and changes list.
- Use the up and down arrow keys to scroll code vertically.
- Show the type of change in the overlapping code area's title.
- When new and deleted files are empty, show `<contents empty>` in code area.
- Use ▓ characters to demarcate diff sections when showing in code area.
- Show authored and committed date information (along with natural time).

### Changed
- Automatically scroll to the top of the code area after selecting a new file or revision.
- Show clean diffs for newly added and deleted files.

### Fixed
- Fix incorrect diffs for changed files when selecting the first commit in a file's history.
- Stop throwing exceptions and show diff when selecting deleted files from files changed panel.

## [0.1.1] - 2020-10-18
### Added
- Show and view list of related files (and their diff) changes along with the selected file.
- Maximize window on app start.
- Area chart now has guides and a moving anchor.
- Show commit position and progress percent in commit information area.

### Changed
- Move the area chart and slider into the center panel.
- The slider thumb starts at the oldest commit instead of the latest.
- Use JetBrains Mono for code font.
- Make commit message, id and author more readable.

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
