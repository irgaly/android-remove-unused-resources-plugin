package io.github.irgaly.gradle.rur

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface RemoveUnusedResourcesExtension {
    /**
     * Don't execute removing resources, only showing resources to be deleted.
     * default: false
     */
    val dryRun: Property<Boolean>

    /**
     * Specify a lint result file (lint-results-{variant}.xml)
     * for `removeUnusedResources` task
     *
     * default:
     * * {buildDir}/reports/lint-results-{default variant}.xml
     */
    val lintResultXml: RegularFileProperty

    /**
     * Exclude resource Id list.
     * This is an entire string to "R.{resource type}.{resource name}"
     */
    val excludeIds: ListProperty<String>

    /**
     * Exclude resource Id regular expression strings.
     * This is a regular expression string to "R.{resource type}.{resource name}"
     * match rule: entire match
     */
    val excludeIdPatterns: ListProperty<String>

    /**
     * Exclude resource file glob patterns.
     * This is glob pattern, the path is relative path from root project's directory.
     * match rule: entire match
     */
    val excludeFilePatterns: ListProperty<String>
}
