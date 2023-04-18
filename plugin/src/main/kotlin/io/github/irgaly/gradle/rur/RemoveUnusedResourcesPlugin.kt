package io.github.irgaly.gradle.rur

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import io.github.irgaly.gradle.rur.extensions.finalizeAgpDsl
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


class RemoveUnusedResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val providers = target.providers
        val extension = target.extensions.create(
            "removeUnusedResources",
            RemoveUnusedResourcesExtension::class.java
        )
        target.tasks.register(
            "removeUnusedResources",
            RemoveUnusedResourcesTask::class.java
        ) { task ->
            target.finalizeAgpDsl {
                var lintResultXml = providers.gradleProperty("rur.lintResultXml").orNull?.let {
                    target.rootProject.file(it)
                } ?: extension.lintResultXml
                if (lintResultXml == null) {
                    val variant =
                        providers.gradleProperty("rur.lintVariant").orNull
                            ?: extension.lintVariant
                            ?: target.getDefaultVariant() ?: ""
                    val fileName =
                        "lint-results${if (variant.isEmpty()) "" else "-$variant"}.xml"
                    lintResultXml =
                        checkNotNull(target.file("${target.buildDir}/reports/$fileName"))
                }
                val dryRun = providers.gradleProperty("rur.dryRun").isPresent
                task.apply {
                    this.dryRun.set(dryRun || (extension.dryRun ?: false))
                    lintVariant.set(extension.lintVariant)
                    this.lintResultXml.set(lintResultXml)
                    excludeIds.set(extension.excludeIds)
                    excludeIdPatterns.set(extension.excludeIdPatterns)
                    excludeFilePatterns.set(extension.excludeFilePatterns)
                    mustRunAfter(target.tasks.withType(AndroidLintTask::class.java))
                }
            }
        }
        val onlyUnusedResources =
            providers.gradleProperty("rur.lint.onlyUnusedResources").isPresent
        val disableLintConfig = providers.gradleProperty("rur.lint.disableLintConfig").isPresent
        val overrideLintConfig = providers.gradleProperty("rur.lint.overrideLintConfig").orNull
        val hasOverrideLintOptions =
            (onlyUnusedResources || disableLintConfig || (overrideLintConfig != null))
        if (hasOverrideLintOptions) {
            target.rootProject.allprojects.forEach { project ->
                project.finalizeAgpDsl {
                    project.extensions.findByType(BaseExtension::class.java)?.lintOptions?.apply {
                        if (onlyUnusedResources) {
                            if (project == target) {
                                xmlReport = true
                                isCheckDependencies = true
                            }
                            isCheckGeneratedSources = true
                            checkOnly.clear()
                            checkOnly("UnusedResources")
                            warning("UnusedResources")
                        }
                        if (disableLintConfig) {
                            target.logger.warn("-Prur.lint.disableLintConfig option is deprecated. Use -Prur.lint.overrideLintConfig instead.")
                            lintConfig =
                                File("${target.rootProject.projectDir}/_dummy_remove_unused_resources.xml")
                        }
                        if (overrideLintConfig != null) {
                            val file = target.rootProject.file(overrideLintConfig)
                            if (!file.exists()) {
                                error("overrideLintConfig file is not exit: $file")
                            }
                            lintConfig = file
                        }
                    }
                }
            }
        }
    }


    /**
     * get default variant from "lint" task's dependencies.
     * ex) "lintDebug" Task
     *
     * if AGP version is lower than 7.0.0, this returns null
     */
    private fun Project.getDefaultVariant(): String? {
        return tasks.findByName("lint")?.let { lintTask ->
            val depends =
                lintTask.dependsOn.filterIsInstance<String>().filter { it.startsWith("lint") }
            if (depends.size == 1) {
                // if AGP 7.0.0 or upper, lint task depends default variant lint task, named "lint{variant}"
                Regex("^lint(.+)$").matchEntire(depends.first())?.groupValues?.get(1)
                    ?.decapitalize()
            } else null
        }
    }
}
