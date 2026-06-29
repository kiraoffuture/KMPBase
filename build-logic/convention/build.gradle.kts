plugins {
    `kotlin-dsl`
}

group = "com.kira.kmpbase.buildlogic"

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "com.kira.kmpbase.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "com.kira.kmpbase.kmp.compose"
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = "com.kira.kmpbase.kmp.feature"
            implementationClass = "KmpFeatureConventionPlugin"
        }
    }
}
