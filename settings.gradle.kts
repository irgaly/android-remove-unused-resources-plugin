enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application").version("7.0.1")
        id("com.android.library").version("7.0.1")
        kotlin("android").version("1.6.10")
        id("org.sample.plugin").version("1.0.0-SNAPSHOT")
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "android-gradle-plugin-skelton"
include(":sample")
includeBuild("plugin")
