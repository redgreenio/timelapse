# Timelapse

Timelapse is an attempt to help developers understand large source files really fast.

## Project setup

### macOS

Run the following script from the cloned project directory, and you should be ready to go!

```shell script
./setup-dev
```

### Windows & Linux

After cloning the project, also clone the **git-testbed** and **simple-android** projects into the `fixtures` directory.

**Git Testbed**

```shell script
git clone git@github.com:redgreenio/git-testbed.git
```

**Simple Android**

```shell script
git clone git@github.com:simpledotorg/simple-android.git
```

```shell script
cd simple-android
git checkout d26b2b56696e63bffa5700488dcfe0154ad8cecd
```

**ProGuard**

Create an empty directory inside the root directory for de-obfuscating ProGuard traces from production.

```shell script
mkdir proguard-lab
```

## Creating a release

```shell script
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
