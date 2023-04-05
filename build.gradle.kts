import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}
