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

1. Update [CHANGELOG](CHANGELOG.md).
2. Bump up the version of the tool after making changes (new features, enhancements, bug fixes, etc). Increment
   the `TOOL_VERSION` constant from the `DevCliCommand.kt` file.
3. After the merging the PR to the `main` branch, tag the commit with `dev-cli/<version>`.
4. Push tag to remote.
5. Update the local installation by following the steps described in section **2. Installation & upgrade**.

```
Copyright 2021 Red Green, Inc.
```
