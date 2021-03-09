# Timelapse

Timelapse onboards developers onto codebases before you make that cup of instant noodles!

## Project setup

### Prerequisite
You should also have access to the [git-testbed](https://github.com/redgreenio/git-testbed) repository.  

### Cloning

The repository has submodules. If you haven't cloned the repository already, run:

```shell
git clone --recursive git@github.com:redgreenio/timelapse.git
```

If you have already cloned the repository without the `--recursive` option. Then `cd` into the project directory and
then run,

```shell
git submodule update --init --recursive
```

### Verification

`cd` into the project directory and run the Gradle `test` task.

**Windows**

```shell
gradlew test
```

***nix**

```shell
./gradlew test
```

If the build succeeds, we are all set!
```shell
> Task :app:test

yada yada yada…

BUILD SUCCESSFUL in 18s
31 actionable tasks: 31 executed
```

### ProGuard

Create an new directory inside the project directory for de-obfuscating ProGuard traces from production.

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
