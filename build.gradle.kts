import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
}

subprojects {
    listOf(
        "org.jetbrains.kotlin.android",
        "org.jetbrains.kotlin.multiplatform"
    ).forEach {
        pluginManager.withPlugin(it) {
            extensions.configure<KotlinProjectExtension> {
                jvmToolchain(11)
            }
        }
    }
}
