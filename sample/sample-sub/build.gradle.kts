plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.sample.app.sample.sub"
    compileSdk = 33
    defaultConfig {
        minSdk = 26
    }
}
