# Changelog

## [Unreleased]

### Added

- Open repository menu item to File menu.

## [2021.0.1] - 2021-04-13

### Added

- Show directories before files in the File Explorer.
- Close current project from the File menu.
- Open recent repositories menu.

### Fixed

- 'Not a git directory' bug if the working tree contains a 'config' directory.
- Weird window transition form welcome screen to the workbench.
- Exit process after closing the window.
- Fixed incorrect names on recent repositories list (Windows).

### Changed

- Sort files and directories in ascending order inside the File Explorer.
- Map Control key shortcuts to Command key on Mac.

## [0.1.9] - 2020-12-12

### Added

- Welcome screen with recent repositories feature.

### Changed

- Allow both command line project launch and welcome screen launch from the same artifact.
- Remove commit SHA displayed in the commit information panel.

### Fixed

- JavaFx UI updates from a non-platform thread.

## [0.1.8] - 2020-12-05

### Changed

- Highlight deletions and insertions in main diff viewer.
- Put commit message below file name in the main diff viewer.

### Fixed

- Remove extraneous vertical line in the middle of the area chart for single commits.

## [0.1.7] - 2020-11-28

### Added

- Show deletions in the area chart.
- Display area chart for single commits.
- From the slider, use `Ctrl + Left Arrow` and `Ctrl + Right Arrow` to navigate to the oldest, and the newest commits in
  one go!

### Fixed

- Seekbar does not move on selecting a file with fewer commits than the previous file.
- Quite a few bugs that went unnoticed in the old area chart.

## [0.1.6] - 2020-11-22

### Fixed

- An application crash due to a non-`URLClassLoader` used in Mac runtime.

## [0.1.5] - 2020-11-21

### Added

- Enhanced diff viewer.
- Show line number in diff viewer.
- Show binary diff message on selecting a binary file.
- Support for file mode changes diff.
- Enable resizing the left (file explorer) and right (changed files & people) panels.
- 'Enter' key brings up diff after changed files pane regains focus.
- Support for non-english languages in the diff viewer.

### Changed

- Always show deleted lines count when showing diff.

### Fixed

- `IncorrectObjectException` while pruning the file tree by date.
- Crash when selecting a modified file from the most recent commit in a pruned tree.
- Moving the slider after selecting a changed file throws an exception.
- Changed files pane freezes after selecting a binary file.

## [0.1.4] - 2020-11-07

### Added

- Prune and display changed files by X days (7, 30, 60, 90) from HEAD in the file explorer.
- Restrict commits between a start and end date for pruned trees.
- Show file count in changed files panel.
- Color code changed file row based on the type of change.
- Click on commit ID to copy information to clipboard.

### Changed

- Replace Swing tree component with JavaFx tree component in file explorer.
- Replace Swing list component with JavaFx list component in changed files.

### Fixed

- Files in initial commit always displayed +1 insertion instead of the actual number of lines.

## [0.1.3] - 2020-10-31

### Added

- People pane to list contributions to a given file.
- Add titled borders to file explorer and changed files.
- Show additions and deletions count along with the file name in code area.

### Changed

- Bring down the worst case load time per node construction for the file explorer from 48.47ms to 1.94ms! Woot woot!

### Fixed

- Changed files list focus on previously selected index even after selecting a new file.
- Disallow selecting multiple files/directories in the file explorer.
- Don't show the selected file in the list of changed files.

## [0.1.2] - 2020-10-25

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
- Squished area chart on smaller window sizes and screen resolutions.

## [0.1.1] - 2020-10-18

### Added

- Show and view list of related files (and their diff) changes along with the selected file.
- Maximize the window on app start.
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

- A crude UI with a file explorer, insertions area chart, code area and a slider.
- Show commit ID, message and author name in description area.
