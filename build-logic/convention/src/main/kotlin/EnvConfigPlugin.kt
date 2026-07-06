import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

abstract class EnvConfigExtension @Inject constructor(
    private val project: Project,
) {
    fun load(appEnv: String): Map<String, String> = EnvConfig.load(project.rootProject, appEnv)

    fun resolveAppEnv(): String = EnvConfig.resolveAppEnv(project.rootProject)
}

abstract class GenerateEnvConfigTask : DefaultTask() {
    @get:Input
    abstract val appEnv: Property<String>

    @get:Input
    abstract val projectRootPath: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val envFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val kotlinOutputDir: DirectoryProperty

    @get:OutputFile
    abstract val iosEnvDevelop: RegularFileProperty

    @get:OutputFile
    abstract val iosEnvStaging: RegularFileProperty

    @get:OutputFile
    abstract val iosEnvProduct: RegularFileProperty

    @TaskAction
    fun generate() {
        val rootDir = File(projectRootPath.get())
        val resolvedAppEnv = appEnv.get()
        EnvConfig.generateKotlin(
            rootDir = rootDir,
            outputDir = kotlinOutputDir.get().asFile,
            appEnv = resolvedAppEnv,
        )
        EnvConfig.generateIosXcconfig(rootDir, "develop", iosEnvDevelop.get().asFile)
        EnvConfig.generateIosXcconfig(rootDir, "staging", iosEnvStaging.get().asFile)
        EnvConfig.generateIosXcconfig(rootDir, "product", iosEnvProduct.get().asFile)
    }
}

class EnvConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("env", EnvConfigExtension::class.java, target)

        if (target.path != ":core:common") return

        val rootProject = target.rootProject
        val generateEnvConfig = rootProject.tasks.register("generateEnvConfig", GenerateEnvConfigTask::class.java) {
            val envInput = rootProject.layout.projectDirectory

            appEnv.set(
                rootProject.providers.of(ResolvedAppEnvValueSource::class.java) {
                    parameters.appEnvProperty.set(rootProject.providers.gradleProperty("appEnv"))
                    parameters.appEnvEnvironment.set(rootProject.providers.environmentVariable("APP_ENV"))
                    parameters.taskNames.set(
                        rootProject.providers.provider {
                            rootProject.gradle.startParameter.taskNames
                        },
                    )
                },
            )
            projectRootPath.set(envInput.asFile.absolutePath)
            envFiles.from(
                envInput.file(".env.develop"),
                envInput.file(".env.staging"),
                envInput.file(".env.product"),
            )
            kotlinOutputDir.set(envInput.dir("core/common/build/generated/kotlin/commonMain/kotlin"))
            iosEnvDevelop.set(envInput.file("iosApp/Configuration/Env.develop.xcconfig"))
            iosEnvStaging.set(envInput.file("iosApp/Configuration/Env.staging.xcconfig"))
            iosEnvProduct.set(envInput.file("iosApp/Configuration/Env.product.xcconfig"))
        }

        wireGenerateEnvConfigDependencies(rootProject, generateEnvConfig)
    }

    private fun wireGenerateEnvConfigDependencies(
        rootProject: Project,
        generateEnvConfig: TaskProvider<GenerateEnvConfigTask>,
    ) {
        rootProject.subprojects {
            plugins.withId("org.jetbrains.kotlin.multiplatform") {
                tasks.configureEach {
                    if (this is org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>) {
                        dependsOn(generateEnvConfig)
                    }
                }
            }
        }
        rootProject.tasks.matching { it.name == "embedAndSignAppleFrameworkForXcode" || it.name == "preBuild" }
            .configureEach { dependsOn(generateEnvConfig) }
    }
}
