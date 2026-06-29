plugins {
    alias(libs.plugins.kmp.compose)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.kira.kmpbase.core.ui.generated.resources"
}

kotlin {
    androidLibrary {
        namespace = "com.kira.kmpbase.core.ui"
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
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.koin.compose)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
