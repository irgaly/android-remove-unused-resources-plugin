# v1.3.0 - 2022/01/25

### Improvements

* [#35](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/35) Add KDoc to
  Extension
* [#37](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/37) Add information
  log when no UnusedResources issues found in lint results.

### Changes

* [#35](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/35) Configuration
  Syntax changed.

use setter functions to set option.

```kotlin
removeUnusedResource {
  excludeIds("R.color.unused_exclude_color")
  excludeIdPatterns("R\\..*exclude_pattern.*")
  excludeFilePatterns("**/values/exclude_colors.xml")
}
```

# v1.2.0 - 2022/01/24

### Changes

* rename excludeFiles option to excludeFilePatterns

### Fixes

* add lintOptions.isCheckGeneratedSources for rur.lint.onlyUnusedResources

# v1.1.1 - 2022/01/24

### Fixes

* Cannot apply lintConfig overriding to all projects.

# v1.1.0 - 2022/01/24

Preserve XML characters.

### Fixes

* Preserve XML special characters, unicode references, empty tags when deleting values resources.

# v1.0.0 - 2022/01/21

First Stable release.

### Feature

* Support exclude options: excludeResourceIds, excludeResourceIdPatterns, excludeFiles

### Changes

* change rur.lintOptionsOnlyUnusedResources to rur.lint.onlyUnusedResources
* change rur.disableLintConfig to rur.lint.disableLintConfig
* change rur.overrideLintConfig -> rur.lint.overrideLintConfig

# v0.9.2 - 2022/01/20

### Feature

* Add rur.disableLintConfig option.

### Changes

* Don't remove resources outside of rootProject directory for safety.

### Fixes

* lintOptionsOnlyUnusedResources option affects to all subprojects.
* removeUnusedResources must run after other lint tasks for one liner

# v0.9.1 - 2022/01/18

### Changes

* Use default variant (lint-results-{default variant}.xml) if both of lintVariant and lintResultXml
  are not specified.
  * in 0.9.0, it uses lint-results.xml.
* Don't delete resources outside of Gradle rootProject.

### Fixes

* Fail gradle commands, if any errors occurred.

# v0.9.0 - 2022/01/18

* initial release
