package org.cufy.mmrpc.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

fun Project.addToKotlinSourceSet(task: Task, directory: File) {
    when {
        plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> {
            addToKotlinJvmSourceSet(task, directory)
        }

        plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
            addToKotlinMultiplatformSourceSet(task, directory)
        }
    }
}

private fun Project.addToKotlinJvmSourceSet(task: Task, directory: File) {
    // configure kotlin compile task to depend on `task`
    tasks.findByPath("compileKotlin")?.dependsOn(task)

    val sourceSets = findProperty("sourceSets")

    if (sourceSets !is SourceSetContainer) return

    sourceSets.findByName("main")?.apply {
        java.srcDir(directory.path)
    }
}

private fun Project.addToKotlinMultiplatformSourceSet(task: Task, directory: File) {
    // configure all kotlin compile tasks to depend on `task`
    project.tasks.withType(KotlinCompile::class.java).all { it.dependsOn(task) }

    val kotlinExtension = project.extensions.findByType(KotlinProjectExtension::class.java)

    kotlinExtension ?: return

    kotlinExtension.sourceSets.findByName("commonMain")?.apply {
        kotlin.srcDir(directory.path)
    }

    // well, here we go.
    tasks.configureEach {
        if (name == "compileCommonMainKotlinMetadata")
            it.dependsOn("generateMMRPCKotlinSources")
        if (name == "compileKotlinJs")
            it.dependsOn("generateMMRPCKotlinSources")
    }
}
