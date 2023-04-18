package io.github.irgaly.gradle.rur

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import io.github.irgaly.gradle.rur.extensions.finalizeAgpDsl
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


class RemoveUnusedResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(
            "removeUnusedResources",
            RemoveUnusedResourcesExtension::class.java
        )
        target.tasks.register(
            "removeUnusedResources",
            RemoveUnusedResourcesTask::class.java
        ) { task ->
            task.apply {
                dryRun.set(extension.dryRun)
                lintVariant.set(extension.lintVariant)
                lintResultXml.set(extension.lintResultXml)
                excludeIds.set(extension.excludeIds)
                excludeIdPatterns.set(extension.excludeIdPatterns)
                excludeFilePatterns.set(extension.excludeFilePatterns)
                mustRunAfter(target.tasks.withType(AndroidLintTask::class.java))
            }
        }
        val onlyUnusedResources =
            target.properties.containsKey("rur.lint.onlyUnusedResources")
        val disableLintConfig = target.properties.containsKey("rur.lint.disableLintConfig")
        val overrideLintConfig = target.properties["rur.lint.overrideLintConfig"] as? String
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
}
