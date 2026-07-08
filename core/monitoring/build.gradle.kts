plugins {
    alias(libs.plugins.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "com.kira.kmpbase.core.monitoring"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.kermit.crashlytics)
            implementation(libs.gitlive.firebase.app)
            implementation(libs.gitlive.firebase.crashlytics)
            implementation(libs.gitlive.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.analytics)
        }
        iosMain.dependencies {
            implementation(libs.kermit.crashlytics)
            implementation(libs.gitlive.firebase.app)
            implementation(libs.gitlive.firebase.crashlytics)
            implementation(libs.gitlive.firebase.analytics)
        }
    }
}
