# Remove Unused Resources Plugin for Android

A gradle plugin to remove unused android resources by Android Lint results xml file.

This is useful for CI because that is provided by gradle task.

# Concept

* This plugin uses Android Lint results xml file for detect unused resources.
  * It supports multi module Android project, because of using Android Lint.
* This plugin provides gradle task.
  * It is suitable to run in CI action.
  * It is equivalent to Android Studio's `Refactor > Remove Unused Resources...` action.

# Usage

Apply the plugin to your app module.

`app/build.gradle.kts`

```kotlin
plugins {
  id("net.irgaly.remove-unused-resources").version("0.9.0")
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

## Run lint only for `UnusedResources`

This plugin provides simple utility for lint, that overrides lint options.

For example, this command let lint to check only `UnusedResources` lint.

```shell
% ./gradlew :app:lintDebug -Prur.lintOptionsOnlyUnusedResources
```

The `-Prur.lintOptionsOnlyUnusedResources` overrides lint options by below settings.

```kotlin
lintOptions {
  // These settings are applied by Plugin automatically, when -Prur.lintOptionsOnlyUnusedResources is specified,
  // so you don't have to add these settings in build.gradle.kts.
  xmlReport = true
  checkOnly.clear()
  checkOnly("UnusedResources")
  warning("UnusedResources")
}
```

## Recommended CI Usage with one liner

This is recommended one liner for CI.

```shell
% ./gradlew :app:lintDebug :app:removeUnusedResources -Prur.lintOptionsOnlyUnusedResources -Prur.lintVariant="debug"
```

This executes below task:

* Run Android Lint with `checkOnly("UnusedResources")`.
  * report will be saved to `app/build/reports/lint-results-debug.xml`
* Clean up unused resources by lint result.
