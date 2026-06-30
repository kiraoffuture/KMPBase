import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(projects.composeApp)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.kira.kmpbase"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.kira.kmpbase"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    flavorDimensions += "environment"
    productFlavors {
        create("develop") {
            dimension = "environment"
            applicationIdSuffix = ".develop"
            versionNameSuffix = ".develop"
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = ".staging"
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
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

androidComponents {
    onVariants { variant ->
        val variantName = variant.name
        val cap = variantName.replaceFirstChar { it.uppercase() }
        val appId = variant.applicationId.get()
        val installTask = tasks.findByName("install$cap")

        tasks.register("run$cap") {
            group = "application"
            description = "Installs and launches the $variantName build."
            if (installTask != null) {
                dependsOn(installTask)
            } else {
                dependsOn("assemble$cap")
            }
            doLast {
                val adb = androidComponents.sdkComponents.adb.get().asFile
                if (installTask == null) {
                    val apk = variant.artifacts.get(com.android.build.api.artifact.SingleArtifact.APK).get().asFile
                    ProcessBuilder(adb.absolutePath, "install", "-r", apk.absolutePath)
                        .inheritIO()
                        .start()
                        .waitFor()
                }
                ProcessBuilder(
                    adb.absolutePath,
                    "shell",
                    "am",
                    "start",
                    "-a",
                    "android.intent.action.MAIN",
                    "-c",
                    "android.intent.category.LAUNCHER",
                    "-p",
                    appId,
                )
                    .inheritIO()
                    .start()
                    .waitFor()
            }
        }
    }
}
