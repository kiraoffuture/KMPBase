import com.android.build.api.artifact.SingleArtifact
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File
import javax.inject.Inject

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id("com.kira.kmpbase.env")
}

val env = extensions.getByType(EnvConfigExtension::class.java)

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(projects.core.monitoring)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.kira.kmpbase"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        resValues = true
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
    flavorDimensions += "environment"
    productFlavors {
        create("develop") {
            dimension = "environment"
        }
        create("staging") {
            dimension = "environment"
        }
        create("product") {
            dimension = "environment"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    productFlavors.configureEach {
        val envMap = env.load(name)
        applicationId = envMap.getValue("APPLICATION_ID")
        versionName = envMap.getValue("APPLICATION_VERSION_NAME")
        versionCode = envMap.getValue("APPLICATION_VERSION_CODE").toInt()
        resValue("string", "app_name", envMap.getValue("APPLICATION_NAME"))
    }

    signingConfigs {
        listOf("develop", "staging", "product").forEach { flavorName ->
            val envMap = env.load(flavorName)
            val keystoreFile = file(envMap.getValue("KEYSTORE_FILE_PATH"))
            if (keystoreFile.exists()) {
                create("env$flavorName") {
                    storeFile = keystoreFile
                    storePassword = envMap.getValue("KEYSTORE_PASSWORD")
                    keyAlias = envMap.getValue("KEY_ALIAS")
                    keyPassword = envMap.getValue("KEY_PASSWORD")
                }
            }
        }
    }

    buildTypes.configureEach {
        listOf("develop", "staging", "product").forEach { flavorName ->
            val signingName = "env$flavorName"
            if (signingConfigs.findByName(signingName) != null) {
                productFlavors.getByName(flavorName).signingConfig = signingConfigs.getByName(signingName)
            }
        }
    }
}

androidComponents {
    onVariants { variant ->
        val variantName = variant.name
        val cap = variantName.replaceFirstChar { it.uppercase() }
        val installTaskName = "install$cap"
        val assembleTaskName = "assemble$cap"
        val isDebug = variant.buildType == "debug"

        tasks.register<LaunchAndroidAppTask>("run$cap") {
            group = "application"
            description = "Installs and launches the $variantName build."
            applicationId.set(variant.applicationId)
            launcherComponent.set(
                variant.applicationId.map { appId ->
                    "$appId/${android.namespace}.MainActivity"
                },
            )
            adbPath.set(androidComponents.sdkComponents.adb.map { it.asFile.absolutePath })
            if (isDebug) {
                dependsOn(installTaskName)
            } else {
                dependsOn(assembleTaskName)
                apkOutputDir.set(
                    variant.artifacts.get(SingleArtifact.APK).map { artifact ->
                        artifact.asFile.absolutePath
                    },
                )
                packagedApkDir.set(
                    layout.buildDirectory
                        .dir("outputs/apk/${variant.flavorName}/${variant.buildType}")
                        .map { it.asFile.absolutePath },
                )
            }
        }
    }
}

abstract class LaunchAndroidAppTask : DefaultTask() {
    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Input
    abstract val applicationId: Property<String>

    @get:Input
    abstract val launcherComponent: Property<String>

    @get:Input
    abstract val adbPath: Property<String>

    @get:Input
    @get:Optional
    abstract val apkOutputDir: Property<String>

    @get:Input
    @get:Optional
    abstract val packagedApkDir: Property<String>

    @TaskAction
    fun launch() {
        val adb = adbPath.get()
        // Prefer final packaged APK (outputs/) over intermediates/, which can be stale or unsigned.
        val apkDirs = listOfNotNull(packagedApkDir.orNull, apkOutputDir.orNull)
        if (apkDirs.isNotEmpty()) {
            val apk = resolveApkPath(*apkDirs.map(::File).toTypedArray())
            val installResult = execOperations.exec {
                isIgnoreExitValue = true
                commandLine(adb, "install", "-r", "-t", apk)
            }
            if (installResult.exitValue != 0) {
                // If the device already has this applicationId signed with a different key,
                // adb returns INSTALL_FAILED_UPDATE_INCOMPATIBLE. Uninstall + retry.
                execOperations.exec {
                    isIgnoreExitValue = true
                    commandLine(adb, "uninstall", applicationId.get())
                }
                execOperations.exec {
                    commandLine(adb, "install", "-r", "-t", apk)
                }
            }
        }
        execOperations.exec {
            commandLine(adb, "shell", "am", "start", "-n", launcherComponent.get())
        }
    }

    private fun resolveApkPath(vararg candidates: File): String {
        val apkFiles = candidates.flatMap { artifact ->
            when {
                artifact.isFile && artifact.extension == "apk" -> listOf(artifact)
                artifact.isDirectory ->
                    artifact.listFiles { file -> file.extension == "apk" }?.toList().orEmpty()
                else -> emptyList()
            }
        }.distinctBy { it.absolutePath }

        if (apkFiles.isEmpty()) {
            error("No APK found in: ${candidates.joinToString { it.absolutePath }}")
        }

        val preferred = apkFiles
            .filterNot { it.name.contains("unsigned", ignoreCase = true) }
            .maxByOrNull { it.length() }
            ?: apkFiles.maxBy { it.length() }
        return preferred.absolutePath
    }
}
