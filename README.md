# Remove Unused Resources Plugin for Android

A gradle plugin to remove unused android resources by Android Lint results xml file.

This is useful for CI because that is provided by gradle task.

# Concepts

* This plugin uses Android Lint results xml file for detect unused resources.
  * It supports multi module Android project, because of using Android Lint.
  * It supports generated sources like DataBinding, Epoxy...
    * Android Lint supports them by checkGeneratedSources option.
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

* Gradle 7.0.2 ~
  * with JVM 17 environment
* Android Gradle Plugin 7.1.0 ~

# Usage

Apply the plugin to your app module.

`app/build.gradle.kts`

```kotlin
plugins {
  id("io.github.irgaly.remove-unused-resources") version "2.1.0"
}
```

Ensure your lint option is correctly set for `UnusedResources` rule.

* `checkGeneratedSources = true` is required, if you use code generation such as DataBinding or
  Epoxy.
  * This is required to each projects that uses code generation.
* `checkDependencies = true` is required, if you use multi module project.
  * This option is default to true from AGP 7.1.0.
  * This option let lint to analyze all resources in all module.

`app/build.gradle.kts`

```kotlin
android {
  ...
  lint {
    // checkGeneratedSources is slow down Android Lint analysing.
    // It is recommended to disable this when you analyse other lint rules.
    checkGeneratedSources = true
    // checkDependencies = true : checkDependencies is true by default
  }
}
```

`othermodule/build.gradle.kts`

```kotlin
android {
  ...
  lint {
    // if a module uses code generation, this is required each project.
    checkGeneratedSources = true
  }
}
```

Run Android Lint, that contains `UnusedResources` analyser.

```shell
% ./gradlew :app:lintDebug
```

Run clean up task, then unused resources are deleted.
There are `removeUnusedResources{variant}` tasks.

```shell
% ./gradlew :app:removeUnusedResourcesDebug
```

console outputs like:

```shell
> Task :app:removeUnusedResourcesDebug
> report from: .../app/src/main/res/values/colors.xml
delete resource element: R.color.black
delete resource element: R.color.unused_color
delete resource element: R.color.unused_color_with_night_theme
...
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

## Run lint only for `UnusedResources` rule

This plugin provides simple utility for lint, that overrides lint options.

For example, this command let lint to check only `UnusedResources` rule.

```shell
% ./gradlew :app:lintDebug -Prur.lint.onlyUnusedResources
```

The `-Prur.lint.onlyUnusedResources` overrides lint options by settings below:

```kotlin
android {
  lint {
    // These settings are applied automatically by the plugin, when -Prur.lint.onlyUnusedResources is specified,
    // so you don't have to add these settings in build.gradle.kts.
    xmlReport = true // only app project
    checkDependencies = true // only app project
    checkGeneratedSources = true
    checkOnly.clear()
    checkOnly.add("UnusedResources")
    warning.add("UnusedResources")
  }
}
```

## Overrides lintConfig

You can overrides a single lintConfig file for all projects.

```shell
% ./gradlew :app:lintDebug -Prur.lint.overrideLintConfig="./lint.unusedresources.xml"
```

The details of lint.xml format
is [here](https://googlesamples.github.io/android-custom-lint-rules/user-guide.html#configuringusinglint.xmlfiles/samplelint.xmlfile)
.

## CI usage example

This is an example for CI usage.

```shell
% ./gradlew :app:lintDebug -Prur.lint.onlyUnusedResources
% ./gradlew :app:removeUnusedResourcesDebug
```

This executes:

* Run Android Lint with `checkOnly.add("UnusedResources")`.
  * Report will be saved to `app/build/reports/lint-results-debug.xml`.
* Clean up unused resources by lint result (`app/build/reports/lint-results-debug.xml`)

# Exclude Resources rules

There are options to set exclude resource ID rules or file path rules.

See [Gradle configuration syntax](#gradle-configuration-syntax) section.

# Gradle task and options

Gradle tasks:

| task                           | description                                                                        |
|--------------------------------|------------------------------------------------------------------------------------|
| removeUnusedResources{variant} | delete unused resources by `{buildDir}/reports/lint-results-{variant}.xml`         |
| removeUnusedResources          | delete unused resources by `{buildDir}/reports/lint-results-{default variant}.xml` |

Gradle properties:

| property                     | description                                                                                                                                                                      | example                                                                                                 |
|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| rur.dryRun                   | only output result, without deletion                                                                                                                                             | `./gradlew :app:removeUnusedResourcesDebug -Prur.dryRun`                                                |
| rur.lintResultXml            | the lint result xml path from rootProject (or full absolute path).  this option is only for `removeUnusedResources` task, it's ignored by `removeUnusedResources{variant}` task. | `./gradlew :app:removeUnusedResources -Prur.lintResultXml="./app/build/reports/lint-results-debug.xml"` |
| rur.lint.onlyUnusedResources | override lint option for checkOnly UnusedResources                                                                                                                               | `./gradlew :app:lintDebug -Prur.lint.onlyUnusedResources`                                               |
| rur.lint.overrideLintConfig  | override lint.lintConfig. the path is relative path from rootProject (or full absolute path)                                                                                     | `./gradlew :app:lintDebug -Prur.lint.overrideLintConfig="./lint.unusedresources.xml"`                   |

# Gradle configuration syntax

Gradle configuration syntax is also available.

`app/build.gradle.kts`

```kotlin
removeUnusedResources {
  // for dry run
  // default: false
  dryRun = true

  // specify lint result xml directly
  // this option is only for `removeUnusedResources` task, it's ignored by `removeUnusedResources{variant}` task.
  // default: {buildDir}/reports/lint-results-{default variant}.xml)
  lintResultXml = file("$buildDir/reports/lint-results-debug.xml")

  // exclude resource Id list. match rule: entire match
  excludeIds.add("R.color.unused_exclude_color")

  // regular expression exclude resource Id list. match rule: regular expression entire match
  excludeIdPatterns.add("R\\..*exclude_pattern.*")

  // glob expression exclude file.
  // file path is relative path from Project's root directory
  excludeFilePatterns.add("**/values/exclude_colors.xml")
}
```

The details of glob pattern is documented
in [JDK FileSystem#getPathMatcher]( https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-)

# Note: Other behaviors

* This plugin does not remove the resources that is **outside of rootProject directory**.
  * Those resources are reported in error message.

# Troubleshoots

## No matching variant of io.github.irgaly.remove-unused-resources:plugin:... was found.

This error is occurred with running Gradle on JVM 11.
Running Gradle on JVM 17 will solve this error.

```shell
...
FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring project ':sample'.
> Could not resolve all files for configuration ':sample:classpath'.
   > Could not resolve io.github.irgaly.remove-unused-resources:plugin:1.4.1.
     Required by:
         project :sample > io.github.irgaly.remove-unused-resources:io.github.irgaly.remove-unused-resources.gradle.plugin:1.4.1
      > No matching variant of io.github.irgaly.remove-unused-resources:plugin:1.4.1 was found. The consumer was configured to find a runtime of a library compatible with Java 11, packaged as a jar, and its dependencies declared externally, as well as attribute 'org.gradle.plugin.api-version' with value '7.4' but:
          - Variant 'apiElements' capability io.github.irgaly.remove-unused-resources:plugin:1.4.1 declares a library, packaged as a jar, and its dependencies declared externally:
              - Incompatible because this component declares an API of a component compatible with Java 17 and the consumer needed a runtime of a component compatible with Java 11
              - Other compatible attribute:
                  - Doesn't say anything about org.gradle.plugin.api-version (required '7.4')
          - Variant 'javadocElements' capability io.github.irgaly.remove-unused-resources:plugin:1.4.1 declares a runtime of a component, and its dependencies declared externally:
              - Incompatible because this component declares documentation and the consumer needed a library
              - Other compatible attributes:
                  - Doesn't say anything about its target Java version (required compatibility with Java 11)
                  - Doesn't say anything about its elements (required them packaged as a jar)
                  - Doesn't say anything about org.gradle.plugin.api-version (required '7.4')
          - Variant 'runtimeElements' capability io.github.irgaly.remove-unused-resources:plugin:1.4.1 declares a runtime of a library, packaged as a jar, and its dependencies declared externally:
              - Incompatible because this component declares a component compatible with Java 17 and the consumer needed a component compatible with Java 11
              - Other compatible attribute:
                  - Doesn't say anything about org.gradle.plugin.api-version (required '7.4')
          - Variant 'sourcesElements' capability io.github.irgaly.remove-unused-resources:plugin:1.4.1 declares a runtime of a component, and its dependencies declared externally:
              - Incompatible because this component declares documentation and the consumer needed a library
              - Other compatible attributes:
                  - Doesn't say anything about its target Java version (required compatibility with Java 11)
                  - Doesn't say anything about its elements (required them packaged as a jar)
                  - Doesn't say anything about org.gradle.plugin.api-version (required '7.4')
...
```
