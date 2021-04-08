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

Not supported yet.

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

```shell
gradlew jpackageImage jpackage
```

This will created a distributable native installer (currently `.dmg`) for **macOS**. Windows and Linux distributions are
yet to be tested.

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
