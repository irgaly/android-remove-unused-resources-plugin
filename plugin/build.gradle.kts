plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publish)
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
version = libs.versions.removeunusedresources.get()

java {
    withSourcesJar()
    withJavadocJar()
}

if (providers.environmentVariable("CI").isPresent) {
    apply(plugin = "signing")
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(
            providers.environmentVariable("SIGNING_PGP_KEY").orNull,
            providers.environmentVariable("SIGNING_PGP_PASSWORD").orNull
        )
    }
}

gradlePlugin {
    website.set("https://github.com/irgaly/android-remove-unused-resources-plugin")
    vcsUrl.set("https://github.com/irgaly/android-remove-unused-resources-plugin")
    plugins {
        create("plugin") {
            id = libs.plugins.removeunusedresources.get().pluginId
            displayName = "Remove Unused Resources Plugin for Android"
            description = "A plugin removes unused resources discovered by Android Lint"
            tags.set(listOf("android"))
            implementationClass = "io.github.irgaly.gradle.rur.RemoveUnusedResourcesPlugin"
        }
    }
}
