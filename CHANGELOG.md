# v2.3.0 - 2024/07/14 JST

* Maintenance
  * Kotlin 2.2.0 [#160](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/160)
  * Support AGP 8.10 ~ 8.11 [#166](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/166)
    * Support Android KMP Library module
 
# v2.2.0 - 2024/05/30 JST

* Maintenance
  * Kotlin 2.0.0 [#133](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/133)

# v2.1.0 - 2023/08/08 JST

* Changes
  * Support Gradle Lazy
    Configuration [#112](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/112)
    * excludeIds, excludeIdPatterns, excludeFilePatterns option uses Property syntax now.

# v2.0.0 - 2023/08/05 JST

This version has breaking changes.

* Breaking Changes
  * `-Prur.lintVariant` and Extension's `lintVariant` options are removed.
  * Please use `removeUnusedResources{variant}` task to specify variant instead.
* Changes
  * AGP 4.X and AGP 7.0 are not supported from this version.
  * Minimum support version is AGP 7.1.

### Changes

* refactor: use
  withPlugin [#103](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/103)
  * fix: you can apply plugins any order
    * apply AGP -> remove-unused-resources
    * apply remove-unused-resources -> AGP
* Register task as
  removeUnusedResources{variant} [#104](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/104)
  * `-Prur.lintVariant` and `lintVariant` options are removed.
* use default variant for removeUnusedResources
  task [#109](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/109)
* Migrate AGP 7.1
  APIs [#100](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/97)
  * lintOptions -> lint migration in this plugin.
  * Using CommonExtension, AndroidComponentsExtension, finalizeDsl, and onVariants APIs.

### Refactor

* mark RemoveUnusedResourcesTask as
  @DisableCachingByDefault [#107](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/107)

### Maintenance

* Kotlin 1.9.0 + Compose Compiler
  1.5.0 [#97](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/97)
* Update dependency gradle to
  v8.2 [#96](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/96)
* Gradle Plugin: jvmToolchain 11 +
  foojay-resolver [#92](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/92)
* CI: update_sample_diff.yml: update
  command [#110](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/110)

# v1.4.1 - 2023/05/9 JST

### Fixes

* error on running task when lintVariant not
  specified [#90](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/90)

# v1.4.0 - 2023/04/18 JST

### Maintenance

* Support Gradle 8 [#85](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/85)
* Support Android Gradle Plugin
  8.0.0 [#85](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/85)
* Sign Gradle Plugin [#84](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/84)

### Changes

* Deprecate rur.lint.disableLintConfig
  option [#85](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/85)

# v1.3.3 - 2022/09/19

### Improvements

* separate and
  use [original-characters-stax-xml-parser](https://github.com/irgaly/original-characters-stax-xml-parser)
* Gradle 7.5.1
* Maven Plugin Publish 1.0.0
* add tests

# v1.3.2 - 2022/01/29

### Changes

* rename syntax to
  removeUnusedResources [#45](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/45)

# v1.3.1 - 2022/01/28

### Fixes

* support AGP 7.1.0 [#43](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/43)
* fix read original xml
  strings [#41](https://github.com/irgaly/android-remove-unused-resources-plugin/pull/41)

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
