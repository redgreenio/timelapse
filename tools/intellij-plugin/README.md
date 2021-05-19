# dev-cli

`dev-cli` is a command line tool to make hard things easier for developers working on the Timelapse project.

## 1. Usage

### `html` subcommand

Generates HTML files from a repository for the specified file with syntax highlighting (only for Kotlin at the moment).

```shell
dev-cli html <file-name> <commit-hash>
```

The command prints the path of the output HTML file.

```shell
Base HTML file written to:
/Users/tomcat/xd-base-html/<file-name>-<commit-hash>.html
```

To see all available options, use the `-h` option.

```shell
dev-cli html -h
```

## 2. Installation & upgrade

### Linux & macOS

```shell
./install-dev-cli.sh
```

### Windows

```shell
install-dev-cli
```

## 3. Release

1. Update the **Changelog**.
2. Bump up the version of the tool after making changes (new features, enhancements, bug fixes, etc). Increment
   the `TOOL_VERSION` constant from the `DevCliCommand.kt` file.
3. After the merging the PR to the `main` branch, tag the commit with `dev-cli/<version>`.
4. Push tag to remote.
5. Update the local installation by following the steps described in section **2. Installation & upgrade**.

## 4. Changelog

### [0.0.11] - 2021-05-19

- Add syntax highlighting to a few Kotlin keywords.

### [0.0.10] - 2021-05-18

- Generated HTML contains the tool  version used to generate the file.
- Tweaks to CSS styles.

### [0.0.9] - 2021-05-17

- Handle newline characters at end of file.

### [0.0.8] - 2021-05-17

- Unmute affected lines of the file in the specified commit.

### [0.0.7] - 2021-05-16

- Show warning message and list paths of files with the same name.

### [0.0.6] - 2021-05-16

- Fix error generating HTML when the repository has multiple files with the same name.

### [0.0.5] - 2021-05-12

- Add installation script for Windows machines.
- Add title to the generated HTML (file name and short commit hash).
- Add `number`, `escape-sequence`, `reserved-symbol`, and `deleted-highlight` CSS properties to generated HTML.
- Clean up CSS in generated HTML.
- Add `-d` `--debug` option to `html` subcommand.

### [0.0.4] - 2021-05-10

- Add `const` CSS property to generated HTML.
- Show error message for missing file names or invalid commit hashes.
- Show error message if the tool is run from a non-git directory.

### [0.0.3] - 2021-05-09

- Run tests before installing the tool locally.
- Remove extraneous quote character `"` from class attributes in generated HTML.

### [0.0.2] - 2021-05-09

- Install script for deploying the cli tool on *nix machines.
- Format console output using colors and font weights.
- Add all CSS styles to generated HTML.
- Mute all lines in the generated file by default.

### [0.0.1] - 2021-05-08
- Generate base HTML file (content in black, no support for newline at end of file)

```
Copyright 2021 Red Green, Inc.
```
