enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "7.3.0"
        id("com.android.library") version "7.3.0"
        kotlin("android") version "1.6.10"
        id("io.github.irgaly.remove-unused-resources") version "1.3.2"
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "android-remove-unused-resources-plugin"
include(":sample", ":sample:sample-sub")
includeBuild("plugin")
