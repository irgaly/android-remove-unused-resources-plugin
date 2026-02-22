plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "org.sample.app.sample.sub"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
}
