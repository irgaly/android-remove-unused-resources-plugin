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
