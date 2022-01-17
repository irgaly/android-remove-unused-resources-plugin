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
    lintVariant = "debug"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
}
