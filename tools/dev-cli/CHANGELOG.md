# Changelog

## [Unreleased]

- Syntax highlight simple string literals.
- Syntax highlight parentheses, curly, square, and angled brackets.
- Syntax highlight integer and boolean literals.
- Syntax highlight commas.

## [0.0.13] - 2021-05-20

- Fix keyword spans enclosing a space character for every keyword in generated HTML.

## [0.0.12] - 2021-05-19

- Add `-nsh` `--no-syntax-highlight` option to `html` subcommand.

## [0.0.11] - 2021-05-19

- Add syntax highlighting to a few Kotlin keywords.

## [0.0.10] - 2021-05-18

- Generated HTML contains the tool  version used to generate the file.
- Tweaks to CSS styles.

## [0.0.9] - 2021-05-17

- Handle newline characters at end of file.

## [0.0.8] - 2021-05-17

- Unmute affected lines of the file in the specified commit.

## [0.0.7] - 2021-05-16

- Show warning message and list paths of files with the same name.

## [0.0.6] - 2021-05-16

- Fix error generating HTML when the repository has multiple files with the same name.

## [0.0.5] - 2021-05-12

- Add installation script for Windows machines.
- Add title to the generated HTML (file name and short commit hash).
- Add `number`, `escape-sequence`, `reserved-symbol`, and `deleted-highlight` CSS properties to generated HTML.
- Clean up CSS in generated HTML.
- Add `-d` `--debug` option to `html` subcommand.

## [0.0.4] - 2021-05-10

- Add `const` CSS property to generated HTML.
- Show error message for missing file names or invalid commit hashes.
- Show error message if the tool is run from a non-git directory.

## [0.0.3] - 2021-05-09

- Run tests before installing the tool locally.
- Remove extraneous quote character `"` from class attributes in generated HTML.

## [0.0.2] - 2021-05-09

- Install script for deploying the cli tool on *nix machines.
- Format console output using colors and font weights.
- Add all CSS styles to generated HTML.
- Mute all lines in the generated file by default.

## [0.0.1] - 2021-05-08
- Generate base HTML file (content in black, no support for newline at end of file)
