plugins {
    alias(libs.plugins.kmp.library)
    id("com.kira.kmpbase.env")
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
            kotlin.srcDir(layout.projectDirectory.dir("build/generated/kotlin/commonMain/kotlin"))
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
