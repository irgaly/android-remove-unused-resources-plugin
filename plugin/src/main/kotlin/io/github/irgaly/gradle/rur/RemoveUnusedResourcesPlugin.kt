package io.github.irgaly.gradle.rur

import com.android.build.api.AndroidPluginVersion
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class RemoveUnusedResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.withAndroid { androidComponents ->
            if (androidComponents.pluginVersion < AndroidPluginVersion(7, 1)) {
                error("please update AGP 7.1.0 or later")
            }
            val extension = extensions.create(
                "removeUnusedResources",
                RemoveUnusedResourcesExtension::class.java
            )
            registerRemoveUnusedResourcesTask(null, extension)
            androidComponents.onVariants { variant ->
                registerRemoveUnusedResourcesTask(variant.name, extension)
            }
            val onlyUnusedResources =
                providers.gradleProperty("rur.lint.onlyUnusedResources").isPresent
            val disableLintConfig = providers.gradleProperty("rur.lint.disableLintConfig").isPresent
            val overrideLintConfig = providers.gradleProperty("rur.lint.overrideLintConfig").orNull
            val hasOverrideLintOptions =
                (onlyUnusedResources || disableLintConfig || (overrideLintConfig != null))
            if (hasOverrideLintOptions) {
                rootProject.allprojects.forEach { project ->
                    project.withAndroid { androidComponents ->
                        androidComponents.finalizeDsl {
                            it.lint {
                                if (onlyUnusedResources) {
                                    if (project == target) {
                                        xmlReport = true
                                        checkDependencies = true
                                    }
                                    checkGeneratedSources = true
                                    checkOnly.clear()
                                    checkOnly.add("UnusedResources")
                                    warning.add("UnusedResources")
                                }
                                if (disableLintConfig) {
                                    logger.warn("-Prur.lint.disableLintConfig option is deprecated. Use -Prur.lint.overrideLintConfig instead.")
                                    lintConfig =
                                        File("${rootProject.projectDir}/_dummy_remove_unused_resources.xml")
                                }
                                if (overrideLintConfig != null) {
                                    val file = rootProject.file(overrideLintConfig)
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
    }

    private fun Project.registerRemoveUnusedResourcesTask(
        taskVariant: String?,
        extension: RemoveUnusedResourcesExtension
    ) {
        tasks.register(
            "removeUnusedResources${taskVariant?.replaceFirstChar { it.uppercase() } ?: ""}",
            RemoveUnusedResourcesTask::class.java
        ) { task ->
            var lintResultXml = if (taskVariant == null) {
                providers.gradleProperty("rur.lintResultXml").orNull?.let {
                    rootProject.file(it)
                } ?: extension.lintResultXml
            } else null
            if (lintResultXml == null) {
                val variant = taskVariant
                    ?: providers.gradleProperty("rur.lintVariant").orNull
                    ?: extension.lintVariant
                if (variant != null) {
                    val fileName =
                        "lint-results${if (variant.isEmpty()) "" else "-$variant"}.xml"
                    lintResultXml =
                        checkNotNull(file("${buildDir}/reports/$fileName"))
                }
            }
            val dryRun = providers.gradleProperty("rur.dryRun").isPresent
            task.apply {
                this.dryRun.set(dryRun || (extension.dryRun ?: false))
                this.lintResultXml.set(lintResultXml)
                excludeIds.set(extension.excludeIds)
                excludeIdPatterns.set(extension.excludeIdPatterns)
                excludeFilePatterns.set(extension.excludeFilePatterns)
                mustRunAfter(tasks.withType(AndroidLintTask::class.java))
            }
        }
    }

    private fun Project.withAndroid(
        action: Project.(androidComponents: AndroidComponentsExtension<*, *, *>) -> Unit
    ) {
        pluginManager.withPlugin("com.android.application") {
            action(
                checkNotNull(
                    extensions.findByType(AndroidComponentsExtension::class.java)
                ) {
                    error("please update AGP 7.1.0 or later")
                }
            )
        }
        pluginManager.withPlugin("com.android.library") {
            action(
                checkNotNull(
                    extensions.findByType(AndroidComponentsExtension::class.java)
                ) {
                    error("please update AGP 7.1.0 or later")
                }
            )
        }
    }
}
