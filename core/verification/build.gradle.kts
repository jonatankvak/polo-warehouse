plugins {
    id("polo.android.library")
}

android {
    namespace = "com.polo.verification"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
