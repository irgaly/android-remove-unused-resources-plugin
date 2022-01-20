enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "7.0.1"
        id("com.android.library") version "7.0.1"
        kotlin("android") version "1.6.10"
        id("io.github.irgaly.remove-unused-resources") version "0.9.2"
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
