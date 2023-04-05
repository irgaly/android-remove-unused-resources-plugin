plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.removeunusedresources)
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "org.sample.app"
        minSdk = 26
        targetSdk = 33
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

removeUnusedResources {
    //dryRun = true
    lintVariant = "debug"
    //lintResultXml = file("${buildDir}/reports/lint-results-debug.xml")
    excludeIds("R.color.unused_exclude_color")
    excludeIdPatterns("R\\..*exclude_pattern.*")
    excludeFilePatterns("**/values/exclude_colors.xml")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    implementation(projects.sample.sampleSub)
}
