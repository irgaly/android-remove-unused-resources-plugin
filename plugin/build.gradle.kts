plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
}

sourceSets.configureEach {
    java.srcDirs("src/$name/kotlin")
}

group = "org.sample.plugin"
version = "1.0.0-SNAPSHOT"

gradlePlugin {
    plugins {
        create("plugin") {
            id = "net.irgaly.gradle.remove-unused-resources"
            implementationClass = "net.irgaly.gradle.rur.RemoveUnusedResourcesPlugin"
        }
    }
}
