# Timelapse
Timelapse is an attempt to help developers understand large source files really fast.

### Project Setup
After cloning the Timelapse project, also clone the **git-testbed** project into the project directory. This can be done by running,

```shell script
git clone git@github.com:redgreenio/git-testbed.git
```

### Helpful Commands
```shell script
git log --oneline -M --stat --follow <file-path>
```
This command is used to retrieve the commit history of the specified file.
