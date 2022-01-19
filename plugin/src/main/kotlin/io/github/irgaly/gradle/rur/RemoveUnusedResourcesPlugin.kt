package io.github.irgaly.gradle.rur

import com.android.build.gradle.BaseExtension
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
            task.dryRun.set(extension.dryRun)
            task.lintVariant.set(extension.lintVariant)
            task.lintResultXml.set(extension.lintResultXml)
        }
        val lintOptionsOnlyUnusedResources =
            target.properties.containsKey("rur.lintOptionsOnlyUnusedResources")
        val disableLintConfig = target.properties.containsKey("rur.disableLintConfig")
        val overrideLintConfig = target.properties["rur.overrideLintConfig"] as? String
        val hasOverrideLintOptions =
            (lintOptionsOnlyUnusedResources || disableLintConfig || (overrideLintConfig != null))
        if (hasOverrideLintOptions) {
            target.allprojects.forEach { project ->
                project.afterEvaluate {
                    project.extensions.findByType(BaseExtension::class.java)?.lintOptions?.apply {
                        if (lintOptionsOnlyUnusedResources) {
                            if (project == target) {
                                xmlReport = true
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
