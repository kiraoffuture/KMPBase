import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.kira.kmpbase.kmp.compose")

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.dependencies {
                    implementation(libs.findLibrary("androidx-navigation-compose").get())
                    implementation(libs.findLibrary("koin-compose").get())
                    implementation(libs.findLibrary("koin-core-viewmodel").get())
                    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                }
            }
        }
    }
}
