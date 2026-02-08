plugins {
    id("polo.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.polo.pallet.api"
}

dependencies {
    implementation(libs.navigation3.runtime)
}
