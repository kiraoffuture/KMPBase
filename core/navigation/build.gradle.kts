plugins {
    alias(libs.plugins.kmp.compose)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.kira.kmpbase.core.navigation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.compose.materialIconsExtended)
        }
    }
}
