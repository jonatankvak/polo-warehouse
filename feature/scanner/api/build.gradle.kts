plugins {
    id("polo.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.polo.scanner.api"
}

dependencies {
    implementation(libs.navigation3.runtime)
    implementation(project(":core:ui"))
}
