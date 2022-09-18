plugins {
    id("com.android.library")
    kotlin("android")
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
}
