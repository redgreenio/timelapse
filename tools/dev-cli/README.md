# dev-cli

`dev-cli` is an internal command line tool to make hard things easier for developers working on the Timelapse project.

## 1. Installation & upgrade

Use the installation scripts present within the project directory to install the tool.

### Linux & macOS

```shell
./install-dev-cli.sh
```

### Windows

```shell
install-dev-cli
```

On installation, the script will print a path to add to your environment variable. Add this path to your local (not
system) environment variable.

```shell
'dev-cli' installed. Please add 'C:\Users\varsh\.dev-cli' to your 'Path' variable.
```

## 2. Usage

### • `html` subcommand

Generates HTML files from a repository for the specified file with syntax highlighting (only for Kotlin at the moment).

```shell
dev-cli html <fileName> <commitHash> [-o=<outputDirectoryPath>]
```

The command prints the path of the output HTML file.

```shell
Base HTML file written to:
/Users/tomcat/xd-base-html/<file-name>-<commit-hash>.html
```

By default, HTML files are written to the **xd-base-html** directory under the user's home directory.

To see all available options, use the `-h` option.

```shell
dev-cli html -h
```

## 3. Release

1. Update [CHANGELOG](CHANGELOG.md).
2. Bump up the version of the tool after making changes (new features, enhancements, bug fixes, etc). Increment
   the `TOOL_VERSION` constant from the `DevCliCommand.kt` file.
3. After the merging the PR to the `main` branch, tag the commit with `dev-cli/<version>`.
4. Push tag to remote.
5. Update the local installation by following the steps described in section **1. Installation & upgrade**.

```
Copyright 2021 Red Green, Inc.
```
