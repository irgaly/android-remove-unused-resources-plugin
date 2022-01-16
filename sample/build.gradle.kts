plugins {
    id("com.android.application")
    kotlin("android")
    id("net.irgaly.remove-unused-resources")
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
    // simple command from xml:
    //   ./gradlew :sample:removeUnusedResources -Prur.lintResultXml="./sample/build/reports/lint-results-debug.xml"
    // simple command from variant:
    //   ./gradlew :sample:removeUnusedResources -Prur.lintVariant="debug"
    // simple command with UnusedResources only lint:
    // ./gradlew :sample:lintDebug :sample:removeUnusedResources -Prur.lintOptionsOnlyUnusedResources -Prur.overrideLintConfig="./lint.unusedresources.xml"

    // configuration:
    dryRun = true
    lintVariant = "debug"
    lintResultXml = file("$buildDir/reports/lint-results-debug.xml")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
}
