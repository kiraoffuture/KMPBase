import org.gradle.api.tasks.PathSensitivity
import java.io.File

plugins {
    alias(libs.plugins.kmp.library)
}

val generateNetworkConfig by tasks.registering {
    notCompatibleWithConfigurationCache("Resolves appEnv from Gradle invocation parameters")
    val envInput = rootProject.layout.projectDirectory
    inputs.files(
        envInput.file(".env.develop"),
        envInput.file(".env.staging"),
        envInput.file(".env.product"),
        envInput.file(".env"),
    ).optional().withPathSensitivity(PathSensitivity.RELATIVE)
    val outputDir = layout.buildDirectory.dir("generated/kotlin/commonMain/kotlin")
    outputs.dir(outputDir)

    doLast {
        fun parseEnvFile(file: File): Map<String, String> = buildMap {
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

        fun normalizeServerUrl(url: String): String =
            if (url.endsWith("/")) url else "$url/"

        fun kotlinStringLiteral(value: String): String =
            "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

        fun resolveAppEnv(): String {
            val envs = listOf("develop", "staging", "product")
            val explicit = (findProperty("appEnv") as? String)?.takeIf { it.isNotBlank() }
                ?: System.getenv("APP_ENV")?.takeIf { it.isNotBlank() }
            if (explicit != null) return explicit

            val fromTask = gradle.startParameter.taskNames.firstOrNull { task ->
                envs.any { env -> task.contains(env, ignoreCase = true) }
            }?.let { task ->
                envs.first { env -> task.contains(env, ignoreCase = true) }
            }
            return fromTask ?: "develop"
        }

        val appEnv = resolveAppEnv()
        val envs = listOf("develop", "staging", "product")
        require(appEnv in envs) {
            "Unsupported appEnv '$appEnv'. Use one of: ${envs.joinToString()}"
        }

        val rootDir = rootProject.layout.projectDirectory.asFile
        val envFile = sequenceOf(
            File(rootDir, ".env.$appEnv"),
            File(rootDir, ".env"),
        ).firstOrNull { it.exists() }
            ?: error(
                "Missing environment file. Create ${File(rootDir, ".env.$appEnv")} " +
                    "(see .env.example)",
            )

        val envMap = parseEnvFile(envFile)
        val serverUrl = normalizeServerUrl(
            envMap["SERVER_URL"] ?: error("SERVER_URL is missing in ${envFile.name}"),
        )

        val packageDir = outputDir.get().asFile.resolve("com/kira/kmpbase/core/common")
        packageDir.mkdirs()
        File(packageDir, "GeneratedNetworkConfig.kt").writeText(
            """
            |package com.kira.kmpbase.core.common
            |
            |internal object GeneratedNetworkConfig {
            |    const val BASE_URL: String = ${kotlinStringLiteral(serverUrl)}
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
        androidMain.dependencies {
            implementation(libs.androidx.security.crypto)
        }
    }
}
