package io.github.irgaly.gradle.rur

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import io.github.irgaly.gradle.rur.extensions.afterEvaluateOrExecute
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


class RemoveUnusedResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(
            "removeUnusedResource",
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
                project.afterEvaluateOrExecute {
                    project.extensions.findByType(BaseExtension::class.java)?.lintOptions?.apply {
                        if (onlyUnusedResources) {
                            if (project == target) {
                                xmlReport = true
                                isCheckGeneratedSources = true
                                isCheckDependencies = true
                            }
                            checkOnly.clear()
                            checkOnly("UnusedResources")
                            warning("UnusedResources")
                        }
                        if (disableLintConfig) {
                            lintConfig = File("")
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
