plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.removeunusedresources)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "org.sample.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "org.sample.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
}

removeUnusedResources {
    //dryRun = true
    //lintResultXml = layout.buildDirectory.file("reports/lint-results-debug.xml")
    excludeIds.add("R.color.unused_exclude_color")
    excludeIdPatterns.add("R\\..*exclude_pattern.*")
    excludeFilePatterns.add("**/values/exclude_colors.xml")
}

dependencies {
    implementation(dependencies.platform(libs.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle)
    implementation(libs.bundles.compose)
    implementation(projects.sample.sampleSub)
}
