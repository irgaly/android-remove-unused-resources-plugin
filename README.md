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

Run clean up task.

```shell
% ./gradlew :app:removeUnusedResources -Prur.lintVariant="Debug"
```


