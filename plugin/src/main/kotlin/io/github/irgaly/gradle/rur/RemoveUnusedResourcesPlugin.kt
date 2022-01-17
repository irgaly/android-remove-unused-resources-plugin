package io.github.irgaly.gradle.rur

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

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
        val overrideLintConfig = target.properties["rur.overrideLintConfig"] as? String
        val hasOverrideLintOptions =
            (lintOptionsOnlyUnusedResources or (overrideLintConfig != null))
        if (hasOverrideLintOptions) {
            target.afterEvaluate { project ->
                project.afterEvaluate {
                    // override lintOptions at most last phase
                    project.extensions.getByType(BaseExtension::class.java).lintOptions.apply {
                        if (lintOptionsOnlyUnusedResources) {
                            xmlReport = true
                            isCheckDependencies = true
                            checkOnly.clear()
                            checkOnly("UnusedResources")
                            warning("UnusedResources")
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
