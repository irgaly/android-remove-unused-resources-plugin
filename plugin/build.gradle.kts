plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

sourceSets.configureEach {
    java.srcDirs("src/$name/kotlin")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.originalCharactersStax)
    testImplementation(libs.test.kotest)
    testImplementation(libs.test.kotest.assertions)
}

group = "io.github.irgaly.remove-unused-resources"
version = "1.3.3"

java {
    withSourcesJar()
    withJavadocJar()
}

gradlePlugin {
    website.set("https://github.com/irgaly/android-remove-unused-resources-plugin")
    vcsUrl.set("https://github.com/irgaly/android-remove-unused-resources-plugin")
    plugins {
        create("plugin") {
            id = "io.github.irgaly.remove-unused-resources"
            displayName = "Remove Unused Resources Plugin for Android"
            description = "A plugin removes unused resources discovered by Android Lint"
            tags.set(listOf("android"))
            implementationClass = "io.github.irgaly.gradle.rur.RemoveUnusedResourcesPlugin"
        }
    }
}
