plugins {
    alias(libs.plugins.kmp.feature)
}

kotlin {
    androidLibrary {
        namespace = "com.kira.kmpbase.feature.settings"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.domain)
        }
    }
}
