plugins {
    id("polo.android.library")
    alias(libs.plugins.ktlint)
    alias(libs.plugins.koin.compiler)
}

android {
    namespace = "com.polo.firebase"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:verification"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.common)
    implementation(libs.firebase.firestore)

    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}
