pluginManagement {
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
    ":libraries:core",
    ":libraries:domain",
    ":libraries:core-data",
    ":feature:authentication",
    ":feature:dashboard",
    ":feature:pallet",
    ":feature:scanner",
    ":libraries:core-ui"
)
