# Timelapse

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

## Creating a release

We use different formats of versioning for public and internal releases.

### Public releases

**Version public releases in the following format:** `[YYYY].[published-artifact-count + 1]`.

Where `[YYYY]` denotes the current year, and `[published-artifact-count]` is the number of publicly released artifacts
in that year so far.

For example, the first public release for the year 2021 would be `2021.1`, and the subsequent release version would be
`2021.2`, and so on.

### Internal releases

**Version internal releases in the following format:** `[YYYY].[published-artifact-count].[build-number]`

We don't distribute internal releases through our public channels. These releases include but are not limited to alpha,
beta, and nightly releases.

For example, the first internal release for 2021 without any public releases would be `2021.0.1`; the second internal release
would be `2021.0.1` and so on. Each internal release will increment its `build-number` by `1`.

### Releases summary
In general, public releases drop the `build-number` from internal releases and increment the `published-artifact-count`
(i.e.), if `2021.3.45` is the latest internal release version; the public release for the release will be `2021.4`.

### Step 1 of 6 - Update CHANGELOG

Underneath the `## [Unreleased]` section, add the version and date in the following format.

```md
## [version] - YYYY-MM-DD
```

Example,
```md
## [2021.0.1] - 2021-04-13
```

### Step 2 of 6 - Update version in code and buildscript

- `TimelapseApp.kt` - update `APP_VERSION` to reflect the current version.
- `app/build.gradle.kts` - update `version` to reflect the current version.
- Stage and commit all changes with the message `build: bump version for release`.

### Step 3 of 6 - Build native package

```shell
gradlew jpackageImage jpackage
```

This will create a distributable native installer (currently `.dmg`) for **macOS**. Windows and Linux distributions are
yet to be tested.

### Step 4 of 6 - Verifying the build

You should see the version number for the build in two places,
1. **The native package** - file name of the binary should contain the version number.
2. **The application (after installation)** - Welcome screen should display the new version number in the bottom right corner.

### Step 5 of 6 - Tag the release

Add a git tag to the release with the version number and push it to remote.

```shell
git tag <version>
```

```shell
git push origin <version>
```

### Step 6 of 6 - Preparing for the next release

**5.1 Determine the version number for the next release.**

If the current release was public, then append `.1`
to the version name. Example, if the current release was `2021.3`, the next would be `2021.3.1`.

If the current release was internal, then increment the `build-number` by 1. For example, if the current release was
`2021.4.10`, the next would be `2021.4.11`.

**5.2 Go to step 2 and update the version accordingly.**

## Distribution

| Operating System | Native Builds | Auto-updates |   Format  |
|------------------|---------------|--------------|-----------|
| macOS            | ✅            | In-progress  | `.dmg`    |
| Windows          | ❌            | ❌           | `-`       |
| Linux            | ❌            | ❌           | `-`       |

## License

```
Copyright 2021 Red Green, Inc.
```
