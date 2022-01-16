plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("maven-publish")
    id("com.gradle.plugin-publish")
}

sourceSets.configureEach {
    java.srcDirs("src/$name/kotlin")
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.4")
}

group = "net.irgaly"
version = "0.9.0"

java {
    withSourcesJar()
    withJavadocJar()
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "net.irgaly.remove-unused-resources"
            displayName = "Remove Unused Resources Plugin for Android"
            description = "A plugin removes unused resources discovered by Android Lint"
            implementationClass = "net.irgaly.gradle.rur.RemoveUnusedResourcesPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/irgaly/android-remove-unused-resources-plugin"
    vcsUrl = "https://github.com/irgaly/android-remove-unused-resources-plugin"
    tags = listOf("android")
}
