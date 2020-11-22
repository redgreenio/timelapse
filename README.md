# Timelapse
Timelapse is an attempt to help developers understand large source files really fast.

### Project setup
After cloning the Timelapse project, also clone the **git-testbed** and **simple-android** projects into the project's root directory.

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

Create an empty directory for de-obfuscating ProGuard traces from production.
```shell script
mkdir proguard-lab
```

### Helpful commands
```shell script
git log --oneline -M --stat --follow <file-path>
```
Use this command to retrieve the commit history of the specified file.

### Creating a release
```shell script
gradlew demoJar
```

After creating a build, copy the `mapping-<version>.txt` and `timelapse-<version>-demo.jar` into the designated Google Drive directory.

### Running on Java 9 and above
Add an environment variable pointing to the JavaFx SDK.

```shell script
export JAVA_FX=/path/to/javafx-sdk-<version>/lib
```

Specify the required JavaFx modules to launch the application.
```shell script
java --module-path $JAVA_FX --add-modules javafx.controls,javafx.web -jar timelapse-<version>.jar /path/to/repo
```

### Quirks
The binary does not ship with JavaFx and will run only on JVMs that include JavaFx library jars.
