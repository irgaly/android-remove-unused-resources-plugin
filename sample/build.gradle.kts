plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.removeunusedresources)
}

android {
    namespace = "org.sample.app"
    compileSdk = 33
    defaultConfig {
        applicationId = "org.sample.app"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

removeUnusedResources {
    //dryRun = true
    //lintResultXml = file("${buildDir}/reports/lint-results-debug.xml")
    excludeIds("R.color.unused_exclude_color")
    excludeIdPatterns("R\\..*exclude_pattern.*")
    excludeFilePatterns("**/values/exclude_colors.xml")
}

dependencies {
    implementation(dependencies.platform(libs.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle)
    implementation(libs.bundles.compose)
    implementation(projects.sample.sampleSub)
}
