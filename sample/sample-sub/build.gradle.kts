plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }
    sourceSets.configureEach {
        java.srcDirs("src/$name/kotlin")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
