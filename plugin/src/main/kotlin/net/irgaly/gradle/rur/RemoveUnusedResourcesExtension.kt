package net.irgaly.gradle.rur

import java.io.File

abstract class RemoveUnusedResourcesExtension {
    var dryRun: Boolean? = null
    var lintVariant: String? = null
    var lintResultXml: File? = null
}
