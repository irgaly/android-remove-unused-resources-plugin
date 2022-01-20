package io.github.irgaly.gradle.rur

import java.io.File

abstract class RemoveUnusedResourcesExtension {
    var dryRun: Boolean? = null
    var lintVariant: String? = null
    var lintResultXml: File? = null
    var excludeIds: List<String> = emptyList()
    var excludeIdPatterns: List<String> = emptyList()
    var excludeFiles: List<String> = emptyList()
}
