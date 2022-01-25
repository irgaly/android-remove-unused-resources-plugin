package io.github.irgaly.gradle.rur

import java.io.File

abstract class RemoveUnusedResourcesExtension {
    /**
     * Don't execute removing resources, only showing resources to be deleted.
     * default: false
     */
    var dryRun: Boolean? = null

    /**
     * Build Variant to find lint result (lint-results-{variant}.xml)
     * default:
     *   AGP 7.0.0 ~: use {buildDir}/reports/lint-results-{default variant}.xml
     *   AGP ~ 4.2.2: use {buildDir}/reports/lint-results.xml
     */
    var lintVariant: String? = null

    /**
     * Specify a lint result file directly (lint-results-{variant}.xml)
     * default:
     *   AGP 7.0.0 ~: use {buildDir}/reports/lint-results-{default variant}.xml
     *   AGP ~ 4.2.2: use {buildDir}/reports/lint-results.xml
     */
    var lintResultXml: File? = null

    /**
     * Exclude resource Id list.
     * This is an entire string to "R.{resource type}.{resource name}"
     */
    var excludeIds: List<String> = emptyList()
        private set

    /**
     * Exclude resource Id regular expression strings.
     * This is a regular expression string to "R.{resource type}.{resource name}"
     */
    var excludeIdPatterns: List<String> = emptyList()
        private set

    /**
     * Exclude resource file glob patterns.
     * This is glob pattern, the path is relative path from root project's directory.
     */
    var excludeFilePatterns: List<String> = emptyList()
        private set

    /**
     * Set exclude resource Id list.
     * This is an entire match string to "R.{resource type}.{resource name}"
     */
    fun excludeIds(vararg ids: String) {
        excludeIds = ids.toList()
    }

    /**
     * Exclude resource Id regular expression strings.
     * This is a regular expression string to "R.{resource type}.{resource name}"
     * match rule: entire match
     */
    fun excludeIdPatterns(vararg idPatterns: String) {
        excludeIdPatterns = idPatterns.toList()
    }

    /**
     * Exclude resource file glob patterns.
     * This is glob pattern, the path is relative path from root project's directory.
     * match rule: entire match
     */
    fun excludeFilePatterns(vararg filePatterns: String) {
        excludeFilePatterns = filePatterns.toList()
    }
}
