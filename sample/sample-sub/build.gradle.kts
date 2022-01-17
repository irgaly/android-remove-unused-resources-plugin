plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 26
        targetSdk = 31
    }
    sourceSets.configureEach {
        java.srcDirs("src/$name/kotlin")
    }
}
