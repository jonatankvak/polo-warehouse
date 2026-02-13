import org.gradle.api.GradleException
import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
}

val verifyModuleBoundaries = tasks.register("verifyModuleBoundaries") {
    group = "verification"
    description = "Verifies architecture dependency boundaries"

    doLast {
        val violations = mutableListOf<String>()

        rootProject.allprojects.forEach { project ->
            project.configurations.forEach { configuration ->
                configuration.dependencies.withType(ProjectDependency::class.java).forEach { dependency ->
                    val sourcePath = project.path
                    val targetPath = dependency.path

                    if (sourcePath.startsWith(":core:") &&
                        (targetPath.startsWith(":feature:") || targetPath == ":app")
                    ) {
                        violations += "$sourcePath must not depend on $targetPath"
                    }

                    if (sourcePath.startsWith(":feature:") && targetPath == ":core:firebase") {
                        violations += "$sourcePath must not depend on $targetPath directly"
                    }

                    if (sourcePath.endsWith(":api") && targetPath == ":core:data") {
                        violations += "$sourcePath API module must not depend on $targetPath directly"
                    }

                    if (sourcePath.startsWith(":feature:") &&
                        !sourcePath.endsWith(":api") &&
                        targetPath == ":core:data"
                    ) {
                        violations += "$sourcePath must not depend on $targetPath directly"
                    }

                    if (sourcePath.endsWith(":api") &&
                        targetPath.startsWith(":feature:") &&
                        targetPath != sourcePath
                    ) {
                        violations += "$sourcePath must not depend on feature module $targetPath"
                    }

                    if (sourcePath.endsWith(":impl") && targetPath.endsWith(":impl") &&
                        sourcePath.substringBeforeLast(":") != targetPath.substringBeforeLast(":")
                    ) {
                        violations += "$sourcePath must not depend on other feature impl $targetPath"
                    }
                }
            }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("Module boundary violations detected:")
                    violations.distinct().sorted().forEach { appendLine(" - $it") }
                }
            )
        }
    }
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn(verifyModuleBoundaries)
}
