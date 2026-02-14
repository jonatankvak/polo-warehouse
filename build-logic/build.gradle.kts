plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly("com.android.tools.build:gradle:9.0.0")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20-Beta2")
}

gradlePlugin {
    plugins {
        register("poloAndroidApplication") {
            id = "polo.android.application"
            implementationClass = "PoloAndroidApplicationConventionPlugin"
        }
        register("poloAndroidApplicationCompose") {
            id = "polo.android.application.compose"
            implementationClass = "PoloAndroidApplicationComposeConventionPlugin"
        }
        register("poloAndroidLibrary") {
            id = "polo.android.library"
            implementationClass = "PoloAndroidLibraryConventionPlugin"
        }
        register("poloAndroidLibraryCompose") {
            id = "polo.android.library.compose"
            implementationClass = "PoloAndroidLibraryComposeConventionPlugin"
        }
    }
}
