import org.gradle.api.tasks.PathSensitivity

plugins {
    alias(libs.plugins.kmp.library)
}

val envFile = rootProject.layout.projectDirectory.file(".env")

val generateNetworkConfig by tasks.registering {
    val envInput = envFile
    inputs.file(envInput).optional().withPathSensitivity(PathSensitivity.RELATIVE)
    val outputDir = layout.buildDirectory.dir("generated/kotlin/commonMain/kotlin")
    outputs.dir(outputDir)

    doLast {
        val envMap = buildMap {
            val file = envInput.asFile
            if (!file.exists()) return@buildMap
            file.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { line ->
                    val idx = line.indexOf('=')
                    if (idx <= 0) return@forEach
                    val key = line.take(idx).trim()
                    val rawValue = line.substring(idx + 1).trim()
                    val value = rawValue
                        .removePrefix("\"")
                        .removeSuffix("\"")
                        .removePrefix("'")
                        .removeSuffix("'")
                    if (key.isNotEmpty()) put(key, value)
                }
        }
        val serverUrl = envMap["SERVER_URL"]?.let { url ->
            if (url.endsWith("/")) url else "$url/"
        } ?: "https://api.example.com/"
        val baseUrlLiteral = "\"" + serverUrl
            .replace("\\", "\\\\")
            .replace("\"", "\\\"") + "\""
        val packageDir = outputDir.get().asFile.resolve("com/kira/kmpbase/core/common")
        packageDir.mkdirs()
        File(packageDir, "GeneratedNetworkConfig.kt").writeText(
            """
            |package com.kira.kmpbase.core.common
            |
            |internal object GeneratedNetworkConfig {
            |    const val BASE_URL: String = $baseUrlLiteral
            |}
            """.trimMargin(),
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    dependsOn(generateNetworkConfig)
}

kotlin {
    androidLibrary {
        namespace = "com.kira.kmpbase.core.common"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    sourceSets {
        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated/kotlin/commonMain/kotlin"))
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
