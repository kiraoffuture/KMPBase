import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")

            extensions.configure<KotlinMultiplatformExtension> {
                listOf(
                    iosArm64(),
                    iosSimulatorArm64(),
                )
                jvm()
            }
        }
    }
}

internal val Project.libs
    get() = extensions.getByType(
        org.gradle.api.artifacts.VersionCatalogsExtension::class.java,
    ).named("libs")
