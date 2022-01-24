# Remove Unused Resources Plugin for Android

A gradle plugin to remove unused android resources by Android Lint results xml file.

This is useful for CI because that is provided by gradle task.

# Concepts

* This plugin uses Android Lint results xml file for detect unused resources.
  * It supports multi module Android project, because of using Android Lint.
* This plugin provides gradle task.
  * It is suitable to run in CI action.
  * It is equivalent to Android Studio's `Refactor > Remove Unused Resources...` action.
* Support all Android's resource type and alternative resources detection.
  * Support all resources (animator, anim, color...)
    in https://developer.android.com/guide/topics/resources/providing-resources#ResourceTypes
  * Support alternative resources (`-<qualifier>`: ex, values-night, drawable-hdpi, values-v26...)
  * Support 9-patch drawable files detection.
* Preserve original characters in XML
  * This plugin can delete unused XML element without modifying other characters and XML structures.
    * example: don't extract `&#{unicode};`, don't replace `>` with `&gt;`, don't replace XML empty
      tag...
* Fast
  * It is fast task because this plugin executes only deleting resource tags or files.
    * Android Lint task may takes long time than this plugin takes :innocent:

# Requires

* Gradle 5.6 ~
  * use new Plugin version management system (settings.gradle.kts + pluginManagement)
* Android Gradle Plugin 4.0.0 ~

# Usage

Add pluginManagement repository settings.

`settings.gradle.kts`

```kotlin
// ...
pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
// ...
```

Apply the plugin to your app module.

`app/build.gradle.kts`

```kotlin
plugins {
  id("io.github.irgaly.remove-unused-resources") version "1.1.1"
}
```

When using AGP 7.0.4 or lower, ensure `isCheckDependencies = true` for multi module project. This
option let lint to analyze all resources in all module.

`app/build.gradle.kts`

```kotlin
lintOptions {
  // if AGP version is 7.1.0 or higher, isCheckDependencies is default to true
  isCheckDependencies = true
}
```

Run Android Lint, that contains `UnusedResources` analyser.

```shell
% ./gradlew :app:lintDebug
```

Run clean up task, then unused resources are deleted.

```shell
% ./gradlew :app:removeUnusedResources -Prur.lintVariant="debug"
```

console outputs like:

```shell
> Task :app:removeUnusedResources
delete resource file: /src/app/src/main/res/drawable/usused_drawable.xml
delete resource element: R.color.usused_color in /src/app/src/main/res/values/colors.xml
delete resource element: R.color.usused_color_with_night_theme in /src/app/src/main/res/values-night/colors.xml
delete resource file because of empty: /src/app/src/main/res/values-night/colors.xml
delete resource element: R.color.usused_color_with_night_theme in /src/app/src/main/res/values/colors.xml
```

modified XML exmaple:

Only modifies unused resource tag, preserve others (XML indent, spaces, special reference characters...) as it is.

```diff
--- a/sample/src/main/res/values/strings.xml
+++ b/sample/src/main/res/values/strings.xml
@@ -1,7 +1,6 @@
 <!-- header comment -->
 <resources>
     <string name="app_name">Plugin Sample</string>
-    <string name="unused_string">unused</string>
     <string name="empty1" />
     <string name="empty2"></string>
     <string name="character_reference">© = &#169;</string>
```

## Run lint only for `UnusedResources`

This plugin provides simple utility for lint, that overrides lint options.

For example, this command let lint to check only `UnusedResources` rule.

```shell
% ./gradlew :app:lintDebug -Prur.lint.onlyUnusedResources
```

The `-Prur.lint.onlyUnusedResources` overrides lint options by settings below:

```kotlin
lintOptions {
  // These settings are applied automatically by the plugin, when -Prur.lint.onlyUnusedResources is specified,
  // so you don't have to add these settings in build.gradle.kts.
  xmlReport = true
  isCheckGeneratedSources = true
  isCheckDependencies = true
  checkOnly.clear()
  checkOnly("UnusedResources")
  warning("UnusedResources")
}
```

## Disable or Overrides lintConfig

This command disables to load default lint.xml.

```shell
% ./gradlew :app:lintDebug -Prur.lint.disableLintConfig
```

The `-Prur.lint.disableLintConfig` overrides lint options by settings below:

```kotlin
lintOptions {
  lintConfig = File("") // invalid file, so it is disabled to load lint.xml
}
```

Also you can overrides a single lintConfig file for all projects.

```shell
% ./gradlew :app:lintDebug -Prur.lint.overrideLintConfig="./lint.unusedresources.xml"
```

The details of lint.xml format
is [here](https://googlesamples.github.io/android-custom-lint-rules/user-guide.html#configuringusinglint.xmlfiles/samplelint.xmlfile)
.

## Recommended CI usage with one liner

This is recommended one liner for CI.

```shell
% ./gradlew :app:lintDebug :app:removeUnusedResources -Prur.lint.onlyUnusedResources -Prur.lint.disableLintConfig -Prur.lintVariant="debug"
```

This executes:

* Run Android Lint with `checkOnly("UnusedResources")` and ignoring lint.xml config.
  * Disabling lint.xml is recommended, because lint.xml settings conflicts checkOnly option.
  * Report will be saved to `app/build/reports/lint-results-debug.xml`.
* Clean up unused resources by lint result (`app/build/reports/lint-results-debug.xml`)

# Exclude Resources rules

There are options to set exclude resource ID rules or file path rules.

See [Gradle configuration syntax](#gradle-configuration-syntax) section.

# Gradle task and options

Gradle tasks:

| task | description |
| --- | --- |
| removeUnusedResources | delete unused resources |

Gradle properties:

| property | description | example |
| --- | --- | --- |
| rur.dryRun | only output result, without deletion | `./gradlew :app:removeUnusedResouces -Prur.dryRun` |
| rur.lintVariant | the variant for lint result xml path. if lintResultXml is set, lintVariant is ignored. use `{buildDir}/reports/lint-results-{default variant}.xml` if no variant is specified in AGP 7.0.0 or upper. | `./gradlew :app:removeUnusedResources -Prur.lintVariant=debug` |
| rur.lintResultXml | the lint result xml path from rootProject (or full absolute path) | `./gradlew :app:removeUnusedResources -Prur.lintResultXml="./app/build/reports/lint-results-debug.xml"` |
| rur.lint.onlyUnusedResources | override lintOptions for checkOnly UnusedResources | `./gradlew :app:lintDebug -Prur.lint.onlyUnusedResources` |
| rur.lint.disableLintConfig | override lintOptions.lintConfig to empty to disable lint.xml loading | `./gradlew :app:lintDebug -Prur.lint.disableLintConfig` |
| rur.lint.overrideLintConfig | override lintOptions.lintConfig. the path is relative path from rootProject (or full absolute path) | `./gradlew :app:lintDebug -Prur.lint.overrideLintConfig="./lint.unusedresources.xml"` |

# Gradle configuration syntax

Gradle configuration syntax is also available.

`app/build.gradle.kts`

```kotlin
removeUnusedResource {
  // for dry run
  // default: false
  dryRun = true
  // specify lint target variant for result xml file detection
  // if lintResultXml is set, lintVariant is ignored.
  // default: not specified (use {buildDir}/reports/lint-results-{default variant}.xml) in AGP 7.0.0 or upper
  lintVariant = "debug"
  // specify lint result xml directly
  // default: not specified (use {buildDir}/reports/lint-results-{default variant}.xml) in AGP 7.0.0 or upper
  lintResultXml = file("$buildDir/reports/lint-results-debug.xml")
  // exclude resource Id list. match rule: entire match
  excludeIds = listOf("R.color.unused_exclude_color")
  // regular expression exclude resource Id list. match rule: regular expression entire match
  excludeIdPatterns = listOf("R\\..*exclude_pattern.*")
  // glob expression exclude file.
  // file path is relative path from Project's root directory
  excludeFilePatterns = listOf("**/values/exclude_colors.xml")
}
```

The details of glob pattern is documented in [JDK FileSystem#getPathMatcher]( https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-)

# Other behaviors

* This plugin does not remove the resources that is **outside of rootProject directory**.
  * Those resources are reported in error message.

# Troubleshoots

## `Could not find com.android.tools.build:gradle:{version}`

This error occurs without pluginManagement > repository settings in settings.gradle.kts.

See [Usage](#usage) section, and add pluginManagement settings.

```shell
org.gradle.api.internal.artifacts.ivyservice.DefaultLenientConfiguration$ArtifactResolveException: Could not resolve all files for configuration ':app:classpath'.
# ...
Caused by: org.gradle.internal.resolve.ModuleVersionNotFoundException: Could not find com.android.tools.build:gradle:7.0.4.
Searched in the following locations:
  - https://plugins.gradle.org/m2/com/android/tools/build/gradle/7.0.4/gradle-7.0.4.pom
If the artifact you are trying to retrieve can be found in the repository but without metadata in 'Maven POM' format, you need to adjust the 'metadataSources { ... }' of the repository declaration.
Required by:
    project :app > io.github.irgaly.remove-unused-resources:io.github.irgaly.remove-unused-resources.gradle.plugin:0.9.1 > io.github.irgaly:plugin:0.9.1
```

# Known Issues

* Android Lint Bug (not this plugin's bug)
  * When project is multi module project with `android.nonTransitiveRClass=true`, and two module has
    same resource ID, Android Lint can't find out that resource is not used.
    * I'm find out this behavior with AGP 7.0.1.
    * example: moduleA has R.drawable.image (this is used) and moduleB has R.drawable.image (this is
      not used), Android Lint is not report moduleB's R.drawable.image
      * related IssueTracker? https://issuetracker.google.com/issues/188871862
