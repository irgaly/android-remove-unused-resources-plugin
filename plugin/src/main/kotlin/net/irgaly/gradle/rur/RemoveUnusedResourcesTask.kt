package net.irgaly.gradle.rur

import net.irgaly.gradle.rur.extensions.getAttributeText
import net.irgaly.gradle.rur.extensions.getElements
import net.irgaly.gradle.rur.extensions.toSequence
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Text
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory.newInstance
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

abstract class RemoveUnusedResourcesTask : DefaultTask() {
    @get:Optional
    @get:Input
    abstract val dryRun: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val lintVariant: Property<String>

    @get:Optional
    @get:InputFile
    abstract val lintResultXml: RegularFileProperty

    @Suppress("LABEL_NAME_CLASH") // for using: return@forEach
    @TaskAction
    fun run() {
        val isDryRun = project.properties.keys.contains("rur.dryRun") or dryRun.getOrElse(false)
        val dryRunMarker = if (isDryRun) "[dry run] " else ""
        var lintResultFile = (project.properties["rur.lintResultXml"] as? String)?.let {
            project.rootProject.file(it)
        } ?: lintResultXml.orNull?.asFile
        if (lintResultFile == null) {
            val variant =
                (project.properties["rur.lintVariant"] as? String) ?: lintVariant.orNull ?: ""
            val fileName =
                "lint-results${if (variant.isEmpty()) "" else "-$variant"}.xml"
            lintResultFile = checkNotNull(project.file("${project.buildDir}/reports/$fileName"))
        }
        logger.debug("lintResultFile = $lintResultFile")
        if (!lintResultFile.exists()) {
            throw IllegalArgumentException("lint report file is not exist: $lintResultFile")
        }
        val lintResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(lintResultFile).documentElement
        if (lintResultDocument.tagName != "issues") {
            error("root tag is not \"issues\": ${lintResultDocument.tagName}")
        }
        lintResultDocument.getElements("issue")
            .filter { it.getAttributeText("id") == "UnusedResources" }
            .forEach { issue ->
                val message = issue.getAttributeText("message")
                if (message == null) {
                    logger.error("message attribute is missing: $issue")
                    return@forEach
                }
                val matchedResource =
                    Regex("^The resource `(R\\.([^.]+)\\.([^.]+))` appears to be unused$").matchEntire(
                        message
                    )
                if (matchedResource == null) {
                    logger.error("unknown message: $message")
                    return@forEach
                }
                val (_, resourceName, resourceType, resourceId) = matchedResource.groupValues
                val location = issue.getElements("location").first()
                val originalTargetFile =
                    project.file(location.attributes.getNamedItem("file").nodeValue)
                val resourceDirectory = originalTargetFile.parentFile.parentFile
                val isValuesResource =
                    Regex("values(-.+)?").matches(originalTargetFile.parentFile.name)
                val directoryName = if (isValuesResource) "values" else resourceType
                val targetDirectories = resourceDirectory.listFiles()?.filter {
                    Regex("$directoryName(-.+)?").matches(it.name)
                } ?: emptyList()
                targetDirectories.flatMap { directory ->
                    directory.listFiles()?.filter {
                        if (isValuesResource) {
                            it.name.endsWith(".xml")
                        } else {
                            (it.nameWithoutExtension == resourceId)
                        }
                    } ?: emptyList()
                }.union(listOf(originalTargetFile)).forEach { targetFile ->
                    if (isValuesResource) {
                        // remove resource element
                        val tagNames = when (resourceType) {
                            "array" -> listOf("array", "integer-array", "string-array")
                            else -> listOf(resourceType)
                        }
                        val document = DocumentBuilderFactory.newInstance().apply {
                            isNamespaceAware = true
                        }.newDocumentBuilder().parse(targetFile)
                        val root = document.documentElement
                        val target = root.childNodes.toSequence().firstOrNull {
                            it.nodeName in tagNames && it.getAttributeText("name")
                                ?.replace(".", "_") == resourceId
                        }
                        if (target == null) {
                            if (originalTargetFile == targetFile) {
                                logger.error("resource not found: $resourceName in $targetFile")
                            }
                            return@forEach
                        }
                        if (target.attributes?.getNamedItemNS(
                                "http://schemas.android.com/tools",
                                "override"
                            )?.nodeValue == "true"
                        ) {
                            logger.info("skip because it has tools:override: $resourceName in $targetFile")
                            return@forEach
                        }
                        logger.info("${dryRunMarker}delete resource element: $resourceName in $targetFile")
                        val targetIndex = root.childNodes.toSequence().indexOf(target)
                        val beforeText = (root.childNodes.item(targetIndex - 1) as? Text)
                        val afterText = (root.childNodes.item(targetIndex + 1) as? Text)
                        if (beforeText != null && afterText != null &&
                            Regex("(\r\n|\\v)\\h*$").containsMatchIn(beforeText.textContent)
                            && Regex("^\\h*(\r\n|\\v)").containsMatchIn(afterText.textContent)
                        ) {
                            // delete target and lines
                            beforeText.textContent =
                                Regex("\\h*$").replace(beforeText.textContent, "")
                            afterText.textContent =
                                Regex("^\\h*(\r\n|\\v)").replace(afterText.textContent, "")
                        } else if (afterText != null) {
                            // delete target and after blanks
                            afterText.textContent =
                                Regex("^\\h*").replace(afterText.textContent, "")
                        }
                        root.removeChild(target)
                        if (root.childNodes.toSequence().all { it is Text }) {
                            // delete empty resource file
                            logger.info("${dryRunMarker}delete resource file because of empty: $targetFile")
                            if (!isDryRun) {
                                targetFile.delete()
                            }
                        } else {
                            // update resource file
                            val xmlText = targetFile.readText()
                            val header = Regex(
                                "^(.*?)<resources.*$",
                                RegexOption.DOT_MATCHES_ALL
                            ).matchEntire(xmlText)?.groupValues?.get(1)
                            val footer = Regex(
                                "^.*</resources>(.*?)$",
                                RegexOption.DOT_MATCHES_ALL
                            ).matchEntire(xmlText)?.groupValues?.get(1)
                            if (header == null || footer == null) {
                                logger.error("cannot parse resources xml header / footer: $header / $footer")
                                return@forEach
                            }
                            val outputText = StringWriter()
                            newInstance().newTransformer()
                                .transform(DOMSource(document), StreamResult(outputText))
                            val outputXml = Regex(
                                "^.*?(<resources.*</resources>).*?$",
                                RegexOption.DOT_MATCHES_ALL
                            ).matchEntire(outputText.toString())?.groupValues?.get(1)
                            if (outputXml == null) {
                                logger.error("cannot parse output xml")
                                return@forEach
                            }
                            if (!isDryRun) {
                                targetFile.writeText("$header$outputXml$footer")
                            }
                        }
                    } else {
                        // delete resource file
                        // target: R.animator, R.anim, R.color, R.drawable, R.mipmap,
                        // R.layout, R.menu, R.raw, R.xml, R.font
                        if (!targetFile.exists()) {
                            logger.error("target file is not exist: $targetFile")
                        } else {
                            logger.info("${dryRunMarker}delete resource file: $targetFile")
                            if (!isDryRun) {
                                targetFile.delete()
                            }
                        }
                    }
                }
            }
    }
}
