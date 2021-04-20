# Retrace

This tool is used to deobfuscate and understand ProGuard stack traces. The version of the base and retrace jars should
match the version used in the gradle plugin.

### Usage

#### Syntax

```shell script
java -jar tools/proguard-retrace-6.2.2.jar <mapping-file> <exception-file>
```

#### Example

```shell script
java -jar tools/proguard-retrace-6.2.2.jar proguard-lab/mapping-0.1.2.txt proguard-lab/exception-0.1.2.txt
```

### Download

The [base](https://repo1.maven.org/maven2/net/sf/proguard/proguard-base/6.2.2/)
and [retrace](https://repo1.maven.org/maven2/net/sf/proguard/proguard-retrace/6.2.2/) jars must be placed in the same
directory. You can replace the version name in the URL of the links to get desired versions of the artifacts. 
