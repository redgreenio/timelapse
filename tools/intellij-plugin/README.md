# IntelliJ Plugin

The **Timelapse Dev** IntelliJ Plugin makes hard tasks easier for working on Timelapse.

## 1. Installation

### Requirement

You should have at least IntelliJ IDEA **2021.1** installed (Community/Ultimate).

### Instructions

Release binaries can be downloaded from the [GitHub releases](https://github.com/redgreenio/timelapse/releases) page.
They are tagged as `intellij-plugin/<version>`.

1. Download the **intellij-plugin-&lt;version&gt;.zip** file.
2. From the IDE, go to *Preferences → Plugins → ⚙️ → Install Plugin From Disk...* and select the downloaded zip file.
3. Restart your IDE for the changes to reflect.

## 2. Usage

The plugin adds a few **Surround With...** live templates for the XD HTML feature. These live templates can be viewed
from
*Preferences → Editor → Live Templates* and under the *Timelapse (XD HTML)* group.

### Shortcut

The live templates are available while working with HTML. To use them, first select the text that you want to surround
with a XD `<span>` tag and hit `⌥ Option` + `⌘ Cmd` + `T` (macOS) to bring the list of available templates and choose
the desired one.

## 3. Building & releasing

### 1. Run the plugin during development

```shell
./gradlew tools:intellij-plugin:runIde
```

### 2. Before release

1. Increment the plugin version inside the `/src/main/resources/META-INF/plugin.xml` file. It is nested within
   the `<version>` tag.
2. Describe the changes to the plugin (new features, enhancements, bug fixes) inside the `<change-notes>` tag.
3. Commit these changes to the repository.

### 3. Build plugin for distribution

```shell
./gradlew tools:intellij-plugin:buildPlugin
```

- This command will return soon, however you may have to wait until the **distributions** directory shows up under
  the **intellij-plugin/build** directory. It could take some time.

- The **distributions** directory will contain the packaged plugin named **intellij-plugin.zip**.

- Rename **intellij-plugin.zip** to **intellij-plugin-&lt;version&gt;.zip**.

### 4. Get the changes merged to `main`

- Raise a PR with the changes and get it merged to the `main` branch.

### 5. Upload the binary to GitHub

- Pull merged changes from remote (`origin`).

- Tag the release commit with `intellij-plugin/<version>`.

- Push the tag to `origin`.

```shell
git push --tags --no-verify
```

- Go to the [Create a new release](https://github.com/redgreenio/timelapse/releases/new) page.

- Specify the tag version (`intellij-plugin/<version>`). This tag should be available on GitHub.

- The release title should be **IntelliJ Plugin v&lt;version&gt;**.

- Add the changelog to the *Describe this release* section.

- Upload the built plugin binary to the release page.

- Check the **This is a pre-release** option.

- Click **Publish release**, and you're done 👏! 

## License

```
Copyright 2021 Red Green, Inc.
```
