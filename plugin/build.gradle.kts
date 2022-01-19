plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("maven-publish")
    id("com.gradle.plugin-publish")
}

sourceSets.configureEach {
    java.srcDirs("src/$name/kotlin")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.4")
    testImplementation("io.kotest:kotest-runner-junit5:5.1.0")
    testImplementation("io.kotest:kotest-assertions-core:5.1.0")
}

group = "io.github.irgaly"
version = "0.9.1"

java {
    withSourcesJar()
    withJavadocJar()
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "io.github.irgaly.remove-unused-resources"
            displayName = "Remove Unused Resources Plugin for Android"
            description = "A plugin removes unused resources discovered by Android Lint"
            implementationClass = "io.github.irgaly.gradle.rur.RemoveUnusedResourcesPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/irgaly/android-remove-unused-resources-plugin"
    vcsUrl = "https://github.com/irgaly/android-remove-unused-resources-plugin"
    tags = listOf("android")
}
