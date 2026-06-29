plugins {
    alias(libs.plugins.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.kira.kmpbase.core.di"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.domain)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.data)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.ktor.client.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
