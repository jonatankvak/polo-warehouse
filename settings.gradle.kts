pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Polo Warehouse"

include(
    ":app",
    ":core:common",
    ":core:model",
    ":core:designsystem",
    ":core:domain",
    ":core:data",
    ":core:firebase",
    ":feature:authentication:api",
    ":feature:authentication:impl",
    ":feature:dashboard:api",
    ":feature:dashboard:impl",
    ":feature:pallet:api",
    ":feature:pallet:impl",
    ":feature:scanner:api",
    ":feature:scanner:impl",
    ":core:ui"
)

project(":core:designsystem").projectDir = file("libraries/core")
project(":core:domain").projectDir = file("libraries/domain")
project(":core:data").projectDir = file("libraries/core-data")
project(":core:ui").projectDir = file("libraries/core-ui")
project(":core").projectDir = file("core")
project(":feature:authentication").projectDir = file("feature/authentication-parent")
project(":feature:dashboard").projectDir = file("feature/dashboard-parent")
project(":feature:pallet").projectDir = file("feature/pallet-parent")
project(":feature:scanner").projectDir = file("feature/scanner-parent")
project(":feature:authentication:impl").projectDir = file("feature/authentication")
project(":feature:dashboard:impl").projectDir = file("feature/dashboard")
project(":feature:pallet:impl").projectDir = file("feature/pallet")
project(":feature:scanner:impl").projectDir = file("feature/scanner")
project(":feature").projectDir = file("feature")
