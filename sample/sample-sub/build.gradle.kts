plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "org.sample.app.sample.sub"
    compileSdk = 37
    defaultConfig {
        minSdk = 26
    }
}
