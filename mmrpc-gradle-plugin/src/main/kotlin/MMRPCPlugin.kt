package org.cufy.mmrpc.gradle

import org.cufy.mmrpc.gradle.kotlin.MMRPCKotlin
import org.cufy.mmrpc.gradle.kotlin.MMRPCKotlinTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class MMRPCPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(MMRPC.EXTENSION_NAME, MMRPCExtension::class.java)
        val kotlinTaskProvider = project.tasks.register(MMRPCKotlin.TASK_NAME, MMRPCKotlinTask::class.java)

        project.afterEvaluate {
            val kotlinTask = kotlinTaskProvider.get()
            kotlinTask.load(extension)
            kotlinTask.prepare()

        }
    }
}
