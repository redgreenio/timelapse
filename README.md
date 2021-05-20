# Timelapse ![CI](https://github.com/redgreenio/timelapse/actions/workflows/build-verification.yml/badge.svg)

Timelapse onboards developers onto codebases before you make that cup of instant noodles!

## 1. System setup

### Step 1 of 3 - IntelliJ IDEA

Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).

### Setup 2 of 3 - JDK 15

Download and install JDK 15 for your OS.

### Setup 3 of 3 - Rune CLI

Install [Node.js](https://nodejs.org/en/) and then run the following command on your machine.

```shell
npm install -g rune-cli
```

## 2. Project setup

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

## 3. Tools

### 1. dev-cli

See [dev-cli's README](/tools/dev-cli/README.md).

### 2. Diff (macOS)

To generate unified diffs for reproducing bugs or handling new use cases, make use of the `diff` utility.

```shell
diff -u a.txt b.txt
```

### 3. ProGuard

ProGuard can come in handy to de-obfuscate stack traces from production.

The runnable jars are placed inside the `/tools/proguard` directory.

## More documentation
* [🚀 Creating a release](./docs/RELEASING.md)

## License

```
Copyright 2021 Red Green, Inc.
```
