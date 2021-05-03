# Timelapse ![CI](https://github.com/redgreenio/timelapse/actions/workflows/build-verification.yml/badge.svg)

Timelapse onboards developers onto codebases before you make that cup of instant noodles!

## Project setup

### Step 1 of 3 - Clone

The repository has submodules. If you haven't cloned the repository already, run:

```shell
git clone --recursive git@github.com:redgreenio/timelapse.git
```

If you have already cloned the repository without the `--recursive` option. Then `cd` into the project directory and
then run,

```shell
git submodule update --init --recursive
```

### Step 2 of 3 - Verify & run

`cd` into the project directory and run the following Gradle tasks.

**Windows**

```shell
gradlew test run
```

**Linux & macOS**

```shell
./gradlew test run
```

If the build succeeds, you should be able to see this screen!

![Verified](docs/images/verified.png)

### Step 3 of 3 - Install Git hooks

**Windows**

```shell
gradlew installGitHooks
```

**Linux & macOS**

```shell
./gradlew installGitHooks
```

### ProGuard

Create a new directory inside the project directory for de-obfuscating ProGuard traces from production.

```shell
mkdir proguard-lab
```

## 🚀 Creating a release

Ensure the working directory is clean before creating a release. Stash or commit changes to a different branch if the working directory is dirty.

### Step 1 of 7 - Get the version for the release

Run the following command to get the version.

```shell
gradlew nextInternalVersion
```

The task will print an output similar to the one shown below.

<pre>
Next INTERNAL version should be: <b>2021.0.2</b>
</pre>

Use the printed version for the release.

### Step 2 of 7 - Update CHANGELOG

Underneath the `## [Unreleased]` section, add the version and date in the following format.

```md
## [version] - YYYY-MM-DD
```

Example,
```md
## [2021.0.2] - 2021-04-13
```

### Step 3 of 7 - Commit changes

Stage and commit changes.

```shell
git add .

git commit -m "docs: update CHANGELOG"
```

### Step 4 of 7 - Build native package

```shell
gradlew jpackageImage jpackage
```

This will create a distributable native installer (currently `.dmg`) for **macOS**. Windows and Linux distributions are
yet to be tested.

### Step 5 of 7 - Verifying the build

You should see the version number for the build in two places,
1. **The native package** - file name of the binary should contain the version number.
2. **The application (after installation)** - Welcome screen should display the new version number in the bottom right corner.

### Step 6 of 7 - Tag the release

Add a git tag to the release with the version number and push it to remote.

```shell
git tag 2021.0.2
```

```shell
git push origin 2021.0.2
```

### Step 7 of 7 - Preparing for the next release

Determine the version number for the next release.

```shell
gradlew nextInternalVersion
```

Use the version number to update the following files.

- `TimelapseApp.kt` - update `APP_VERSION` to reflect the current version.
- `app/build.gradle.kts` - update `version` to reflect the current version.

Stage and commit changes.

```shell
git add .

git commit -m "build: prepare for next development version"
```

## Distribution

| Operating System | Native Builds | Auto-updates |   Format  |
|------------------|---------------|--------------|-----------|
| macOS            | ✅            | In-progress  | `.dmg`    |
| Windows          | ❌            | ❌           | `-`       |
| Linux            | ❌            | ❌           | `-`       |

# Other tools

To generate unified diffs for reproducing bugs or handling new use cases, make use of the `diff` utility.

```shell
diff -u a.txt b.txt
```

## License

```
Copyright 2021 Red Green, Inc.
```
