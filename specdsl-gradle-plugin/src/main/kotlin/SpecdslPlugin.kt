package org.cufy.specdsl.gradle

import org.cufy.specdsl.gradle.kotlin.SpecdslKotlin
import org.cufy.specdsl.gradle.kotlin.SpecdslKotlinTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class SpecdslPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(Specdsl.EXTENSION_NAME, SpecdslExtension::class.java)
        val kotlinTaskProvider = project.tasks.register(SpecdslKotlin.TASK_NAME, SpecdslKotlinTask::class.java)

        project.afterEvaluate {
            val kotlinTask = kotlinTaskProvider.get()
            kotlinTask.load(extension)
            kotlinTask.prepare()

        }
    }
}
