# v1.0.0 - 2022/01/21

First Stable release.

## Feature

* Support exclude options: excludeResourceIds, excludeResourceIdPatterns, excludeFiles

## Changes

* change rur.lintOptionsOnlyUnusedResources to rur.lint.onlyUnusedResources
* change rur.disableLintConfig to rur.lint.disableLintConfig
* change rur.overrideLintConfig -> rur.lint.overrideLintConfig

# v0.9.2 - 2022/01/20

## Feature

* Add rur.disableLintConfig option.

## Changes

* Don't remove resources outside of rootProject directory for safety.

## FIX

* lintOptionsOnlyUnusedResources option affects to all subprojects.
* removeUnusedResources must run after other lint tasks for one liner

# v0.9.1 - 2022/01/18

## Changes

* Use default variant (lint-results-{default variant}.xml) if both of lintVariant and lintResultXml
  are not specified.
  * in 0.9.0, it uses lint-results.xml.
* Don't delete resources outside of Gradle rootProject.

## Fixes

* Fail gradle commands, if any errors occurred.

# v0.9.0 - 2022/01/18

* initial release
