package io.github.irgaly.gradle.rur

import com.android.build.api.AndroidPluginVersion
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.lint.AndroidLintTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class RemoveUnusedResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val lintVariantOption = target.providers.gradleProperty("rur.lintVariant").orNull
        if (lintVariantOption != null) {
            error("rur.lintVariant parameter is deprecated. Use removeUnusedResources${lintVariantOption.replaceFirstChar { it.uppercase() }} Task instead.")
        }
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
                registerRemoveUnusedResourcesTask(variant, extension)
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
        variant: Variant?,
        extension: RemoveUnusedResourcesExtension
    ) {
        val names = if (variant == null) {
            listOf(null)
        } else listOf(variant.name, variant.buildType)
        names.forEach { variantName ->
            val taskName =
                "removeUnusedResources${variantName?.replaceFirstChar { it.uppercase() } ?: ""}"
            if (tasks.findByName(taskName) == null) {
                tasks.register(
                    taskName,
                    RemoveUnusedResourcesTask::class.java
                ) { task ->
                    var lintResultXml = if (variant == null) {
                        providers.gradleProperty("rur.lintResultXml").orNull?.let {
                            rootProject.file(it)
                        } ?: extension.lintResultXml
                    } else null
                    if (lintResultXml == null) {
                        val targetVariant = variantName ?: getDefaultVariant()
                        val fileName = "lint-results-$targetVariant.xml"
                        lintResultXml = checkNotNull(file("${buildDir}/reports/$fileName"))
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

    /**
     * get default variant from "lint" task's dependencies.
     * ex) "lintDebug" Task
     */
    private fun Project.getDefaultVariant(): String {
        val variant = tasks.findByName("lint")?.let { lintTask ->
            val depends =
                lintTask.dependsOn.filterIsInstance<String>().filter { it.startsWith("lint") }
            if (depends.size == 1) {
                // lint task depends default variant lint task, named "lint{variant}"
                Regex("^lint(.+)$").matchEntire(depends.first())?.groupValues?.get(1)
                    ?.replaceFirstChar { it.lowercase() }
            } else null
        }
        checkNotNull(variant) { "no default variant found" }
        return variant
    }
}
