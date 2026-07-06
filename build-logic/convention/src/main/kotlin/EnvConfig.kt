import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import java.io.File

object EnvConfig {
    val ENV_NAMES = listOf("develop", "staging", "product")

    val REQUIRED_KEYS = listOf(
        "SERVER_URL",
        "APPLICATION_NAME",
        "APPLICATION_VERSION_NAME",
        "APPLICATION_VERSION_CODE",
        "APPLICATION_ID",
        "KEYSTORE_FILE_PATH",
        "KEYSTORE_PASSWORD",
        "KEY_ALIAS",
        "KEY_PASSWORD",
    )

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

    fun resolveAppEnv(project: Project): String =
        resolveAppEnvFromTaskNames(
            explicitAppEnv = project.findProperty("appEnv") as? String,
            appEnvEnvironment = System.getenv("APP_ENV"),
            taskNames = project.gradle.startParameter.taskNames,
        )

    fun resolveAppEnvFromTaskNames(
        explicitAppEnv: String?,
        appEnvEnvironment: String?,
        taskNames: List<String>,
    ): String {
        explicitAppEnv?.takeIf { it.isNotBlank() }?.let { return it }
        appEnvEnvironment?.takeIf { it.isNotBlank() }?.let { return it }

        val fromTask = taskNames.firstOrNull { task ->
            ENV_NAMES.any { env -> task.contains(env, ignoreCase = true) }
        }?.let { task ->
            ENV_NAMES.first { env -> task.contains(env, ignoreCase = true) }
        }
        return fromTask ?: "develop"
    }

    fun envFileFor(rootDir: File, appEnv: String): File =
        sequenceOf(
            File(rootDir, ".env.$appEnv"),
            File(rootDir, ".env"),
        ).firstOrNull { it.exists() }
            ?: error(
                "Missing environment file. Create ${File(rootDir, ".env.$appEnv")} (see .env.example)",
            )

    fun load(rootDir: File, appEnv: String): Map<String, String> {
        require(appEnv in ENV_NAMES) {
            "Unsupported appEnv '$appEnv'. Use one of: ${ENV_NAMES.joinToString()}"
        }
        val envMap = parseEnvFile(envFileFor(rootDir, appEnv))
        REQUIRED_KEYS.forEach { key ->
            require(!envMap[key].isNullOrBlank()) {
                "$key is missing in .env.$appEnv"
            }
        }
        return envMap
    }

    fun load(project: Project, appEnv: String): Map<String, String> =
        load(project.rootProject.layout.projectDirectory.asFile, appEnv)

    fun generateKotlin(rootDir: File, outputDir: File, appEnv: String) {
        val envMap = load(rootDir, appEnv)
        val serverUrl = normalizeServerUrl(envMap.getValue("SERVER_URL"))
        val versionCode = envMap.getValue("APPLICATION_VERSION_CODE").toIntOrNull()
            ?: error("APPLICATION_VERSION_CODE must be an integer in .env.$appEnv")

        val packageDir = outputDir.resolve("com/kira/kmpbase/core/common")
        packageDir.mkdirs()
        File(packageDir, "GeneratedEnvConfig.kt").writeText(
            """
            |package com.kira.kmpbase.core.common
            |
            |internal object GeneratedEnvConfig {
            |    const val BASE_URL: String = ${kotlinStringLiteral(serverUrl)}
            |    const val APPLICATION_NAME: String = ${kotlinStringLiteral(envMap.getValue("APPLICATION_NAME"))}
            |    const val APPLICATION_VERSION_NAME: String = ${kotlinStringLiteral(envMap.getValue("APPLICATION_VERSION_NAME"))}
            |    const val APPLICATION_VERSION_CODE: Int = $versionCode
            |    const val APPLICATION_ID: String = ${kotlinStringLiteral(envMap.getValue("APPLICATION_ID"))}
            |}
            """.trimMargin(),
        )
    }

    fun generateIosXcconfig(rootDir: File, appEnv: String, output: File) {
        val envMap = load(rootDir, appEnv)
        output.parentFile.mkdirs()
        output.writeText(
            """
            |// Generated from .env.$appEnv — do not edit manually.
            |APP_DISPLAY_NAME=${envMap.getValue("APPLICATION_NAME")}
            |INFOPLIST_KEY_CFBundleDisplayName=${'$'}(APP_DISPLAY_NAME)
            |MARKETING_VERSION=${envMap.getValue("APPLICATION_VERSION_NAME")}
            |CURRENT_PROJECT_VERSION=${envMap.getValue("APPLICATION_VERSION_CODE")}
            |PRODUCT_BUNDLE_IDENTIFIER=${envMap.getValue("APPLICATION_ID")}
            """.trimMargin(),
        )
    }
}

abstract class ResolvedAppEnvValueSource : ValueSource<String, ResolvedAppEnvValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        val appEnvProperty: Property<String>
        val appEnvEnvironment: Property<String>
        val taskNames: org.gradle.api.provider.ListProperty<String>
    }

    override fun obtain(): String =
        EnvConfig.resolveAppEnvFromTaskNames(
            explicitAppEnv = parameters.appEnvProperty.orNull,
            appEnvEnvironment = parameters.appEnvEnvironment.orNull,
            taskNames = parameters.taskNames.get(),
        )
}
