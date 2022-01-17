# Remove Unused Resources Plugin for Android

A gradle plugin to remove unused android resources by Android Lint results xml file.

This provides a gradle task, so it is useful for CI.

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
> Task :sample:removeUnusedResources
delete resource file: /src/sample/src/main/res/drawable/usused_drawable.xml
delete resource element: R.color.usused_color in /src/sample/src/main/res/values/colors.xml
delete resource element: R.color.usused_color_with_night_theme in /src/sample/src/main/res/values-night/colors.xml
delete resource file because of empty: /src/sample/src/main/res/values-night/colors.xml
delete resource element: R.color.usused_color_with_night_theme in /src/sample/src/main/res/values/colors.xml
```


