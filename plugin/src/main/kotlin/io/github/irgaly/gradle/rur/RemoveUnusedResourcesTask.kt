package io.github.irgaly.gradle.rur

import io.github.irgaly.gradle.rur.extensions.containsInDescendants
import io.github.irgaly.gradle.rur.extensions.getAttributeText
import io.github.irgaly.gradle.rur.extensions.getElements
import io.github.irgaly.gradle.rur.xml.getAttributeValue
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.StringWriter
import java.nio.file.FileSystems
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory

abstract class RemoveUnusedResourcesTask : DefaultTask() {
    @get:Input
    abstract val dryRun: Property<Boolean>

    @get:Optional
    @get:InputFile
    abstract val lintResultXml: RegularFileProperty

    @get:Optional
    @get:Input
    abstract val excludeIds: ListProperty<String>

    @get:Optional
    @get:Input
    abstract val excludeIdPatterns: ListProperty<String>

    @get:Optional
    @get:Input
    abstract val excludeFilePatterns: ListProperty<String>

    @Suppress("LABEL_NAME_CLASH") // for using: return@forEach
    @TaskAction
    fun run() {
        val isDryRun = dryRun.get()
        val dryRunMarker = if (isDryRun) "[dry run] " else ""
        var lintResultFile = lintResultXml.orNull?.asFile
            ?: error("Could not determine lintResultXml file. You should set a lintVariant option or lintResultXml option directly")
        logger.info("lintResultFile = $lintResultFile")
        val excludeResourceNames = (excludeIds.orNull?.toHashSet() ?: emptySet())
        val excludeResourceNamePatterns =
            (excludeIdPatterns.orNull?.map { it.toRegex() } ?: emptyList())
        val excludeFileMatchers = FileSystems.getDefault().let { fileSystem ->
            excludeFilePatterns.orNull?.map {
                fileSystem.getPathMatcher("glob:$it")
            } ?: emptyList()
        }
        if (!lintResultFile.exists()) {
            throw IllegalArgumentException("lint report file is not exist: $lintResultFile")
        }
        val lintResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(lintResultFile).documentElement
        if (lintResultDocument.tagName != "issues") {
            error("root tag is not \"issues\": ${lintResultDocument.tagName}")
        }
        var lintResultUnusedResourcesIssueCount = 0
        val issueTargets: MutableList<IssueTarget> = mutableListOf()
        lintResultDocument.getElements("issue")
            .filter { it.getAttributeText("id") == "UnusedResources" }
            .forEach { issue ->
                lintResultUnusedResourcesIssueCount++
                val message = issue.getAttributeText("message")
                    ?: error("message attribute is missing: $issue")
                val matchedResource =
                    Regex("^The resource `(R\\.([^.]+)\\.([^.]+))` appears to be unused$").matchEntire(
                        message
                    ) ?: error("unknown message: $message")
                val (_, resourceName, resourceType, resourceId) = matchedResource.groupValues
                if (excludeResourceNames.contains(resourceName) ||
                    excludeResourceNamePatterns.any { it.matches(resourceName) }
                ) {
                    logger.debug("ignore because exclude resource id: $resourceName")
                    return@forEach
                }
                val location = issue.getElements("location").first()
                val originalTargetFile = File(location.attributes.getNamedItem("file").nodeValue)
                if (!originalTargetFile.isAbsolute) {
                    error("target file is relative path: $originalTargetFile")
                }
                if (listOf(project.rootProject).union(project.rootProject.subprojects).all {
                        // check rootProject first, then check subprojects
                        // because rootProject is most likely top of all project's directory.
                        // any subproject placed in outside of rootProject is rare situation.
                        !it.projectDir.containsInDescendants(originalTargetFile)
                    }) {
                    logger.warn("skip: target file is outside of all project's directory: $originalTargetFile")
                    return@forEach
                }
                val resourceDirectory = originalTargetFile.parentFile.parentFile
                val isValuesResource =
                    Regex("values(-.+)?").matches(originalTargetFile.parentFile.name)
                issueTargets.add(
                    IssueTarget(
                        IssueTargetGroup(isValuesResource, resourceDirectory),
                        resourceName,
                        resourceType,
                        resourceId,
                        originalTargetFile
                    )
                )
            }
        if (lintResultUnusedResourcesIssueCount == 0) {
            logger.lifecycle("Lint Report has no UnusedResources issues: $lintResultFile")
        }
        val groupedIssueTargets = issueTargets.groupBy { it.group }
        groupedIssueTargets.filter { it.key.isValuesResource }.forEach { (issueGroup, issues) ->
            val originalTargetFiles = issues.map { it.originalTargetFile }.distinct().toSet()
            issueGroup.resourceDirectory.listFiles()?.filter {
                Regex("values(-.+)?").matches(it.name)
            }?.flatMap { directory ->
                directory.listFiles()?.filter {
                    it.name.endsWith(".xml")
                } ?: emptyList()
            }.let { targetFiles ->
                originalTargetFiles.union(targetFiles ?: emptyList()).toList()
            }.filterNot { targetFile ->
                // ignore exclude file
                excludeFileMatchers.any {
                    it.matches(targetFile.relativeTo(project.rootDir).toPath())
                }
            }.forEach { targetFile ->
                if ((targetFile in originalTargetFiles) && !targetFile.exists()) {
                    logger.warn("target file is not exist: $targetFile")
                    return@forEach
                }
                // remove resource element
                val removed: MutableSet<IssueTarget> = mutableSetOf()
                val skipped: MutableSet<IssueTarget> = mutableSetOf()
                var remainResources = false
                val converter = XmlConverter { startElementEvent ->
                    var remove = false
                    if (startElementEvent.level == 1) {
                        // only check root <resources>'s child elements
                        val target = startElementEvent.event.asStartElement()
                        val tagName = target.name.toString()
                        val attribute = target.getAttributeValue("name")
                        val resourceId = attribute?.replace(".", "_")
                        val issue = issues.firstOrNull { issue ->
                            tagName in when (issue.resourceType) {
                                "array" -> listOf("array", "integer-array", "string-array")
                                else -> listOf(issue.resourceType)
                            } && resourceId == issue.resourceId
                        }
                        if (issue != null) {
                            val overrideName = QName(
                                "http://schemas.android.com/tools",
                                "override"
                            )
                            if (target.getAttributeValue(overrideName) == "true") {
                                skipped.add(issue)
                            } else {
                                remove = true
                                removed.add(issue)
                            }
                        }
                        if (!remove) {
                            remainResources = true
                        }
                    }
                    XmlConverter.Operation(remove)
                }
                val output = StringWriter()
                converter.convert(targetFile.inputStream(), output)
                if (skipped.isNotEmpty() || removed.isNotEmpty()) {
                    logger.lifecycle("> report from: $targetFile")
                }
                issues.forEach { issue ->
                    when {
                        issue in skipped -> {
                            logger.lifecycle("skip because it has tools:override: ${issue.resourceName}")
                        }
                        issue in removed -> {
                            logger.lifecycle("${dryRunMarker}delete resource element: ${issue.resourceName}")
                        }
                        (issue.originalTargetFile == targetFile) -> {
                            logger.warn("resource not found: ${issue.resourceName}")
                        }
                    }
                }
                if (remainResources) {
                    if (!isDryRun) {
                        // update resource file
                        targetFile.writeText(output.toString())
                    }
                } else {
                    // delete empty resource file
                    logger.lifecycle("${dryRunMarker}delete resource file because of empty: $targetFile")
                    if (!isDryRun) {
                        targetFile.delete()
                    }
                }
            }
        }
        groupedIssueTargets.filter { !it.key.isValuesResource }.forEach { (issueGroup, issues) ->
            issues.forEach { issue ->
                val targetFiles = issueGroup.resourceDirectory.listFiles()?.filter {
                    Regex("${issue.resourceType}(-.+)?").matches(it.name)
                }?.flatMap { directory ->
                    directory.listFiles()?.filter {
                        (Regex("\\.9$").replace(
                            it.nameWithoutExtension,
                            ""
                        ) == issue.resourceId)
                    } ?: emptyList()
                }.let { listOf(issue.originalTargetFile).union(it ?: emptyList()).toList() }
                // ignore resource if any file is matched
                if (targetFiles.any { targetFile ->
                        excludeFileMatchers.any {
                            it.matches(targetFile.relativeTo(project.rootDir).toPath())
                        }
                    }) {
                    logger.debug("ignore because exclude file matched: ${issue.resourceName}")
                    return@forEach
                }
                targetFiles.forEach { targetFile ->
                    if ((issue.originalTargetFile == targetFile) && !targetFile.exists()) {
                        logger.warn("target file is not exist: $targetFile")
                        return@forEach
                    }
                    // delete resource file
                    // target: R.animator, R.anim, R.color, R.drawable, R.mipmap,
                    // R.layout, R.menu, R.raw, R.xml, R.font
                    logger.lifecycle("${dryRunMarker}delete resource file: $targetFile")
                    if (!isDryRun) {
                        targetFile.delete()
                    }
                }
            }
        }
    }

    private data class IssueTargetGroup(
        val isValuesResource: Boolean,
        val resourceDirectory: File
    )

    private data class IssueTarget(
        val group: IssueTargetGroup,
        val resourceName: String,
        val resourceType: String,
        val resourceId: String,
        val originalTargetFile: File
    )
}
