plugins {
    id("polo.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.polo.firebase"
}

dependencies {
    implementation(project(":core:data"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.common)
    implementation(libs.firebase.firestore)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
