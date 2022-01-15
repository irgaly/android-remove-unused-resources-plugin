package net.irgaly.gradle.rur

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
    }
}
