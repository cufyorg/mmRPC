package org.cufy.mmrpc.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class MmrpcPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(Mmrpc.EXTENSION_NAME, MmrpcExtension::class.java)

        project.tasks.register(
            Mmrpc.Kotlin.GENERATE_SOURCES_TASK_NAME,
            MmrpcKotlinGenerateSourcesTask::class.java,
        ).also { taskProvider ->
            project.afterEvaluate {
                val task = taskProvider.get()
                task.load(extension)
                task.prepare()
            }
        }
    }
}
