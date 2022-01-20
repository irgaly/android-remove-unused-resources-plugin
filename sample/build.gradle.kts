plugins {
    id("com.android.application")
    kotlin("android")
    id("io.github.irgaly.remove-unused-resources")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "org.sample.app"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets.configureEach {
        java.srcDirs("src/$name/kotlin")
    }
    buildFeatures {
        dataBinding = true
    }
}

removeUnusedResource {
    //dryRun = true
    lintVariant = "debug"
    //lintResultXml = file("${buildDir}/reports/lint-results-debug.xml")
    excludeIds = listOf("R.color.unused_exclude_color")
    excludeIdPatterns = listOf("R\\..*exclude_pattern.*")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    implementation(projects.sample.sampleSub)
}
