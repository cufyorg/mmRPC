package org.cufy.specdsl.gradle.kotlin

import kotlinx.serialization.json.Json
import org.cufy.specdsl.compact.CompactSpecSheet
import org.cufy.specdsl.compact.inflate
import org.cufy.specdsl.gen.kotlin.GenFeature
import org.cufy.specdsl.gen.kotlin.generateFileSpecSet
import org.cufy.specdsl.gen.kotlin.util.signatureOf
import org.cufy.specdsl.gradle.Specdsl
import org.cufy.specdsl.gradle.SpecdslExtension
import org.cufy.specdsl.gradle.addToKotlinSourceSet
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import java.io.File
import java.io.IOException

private const val TAG = "SPECDSL_KOTLIN"

open class SpecdslKotlinTask : DefaultTask() {
    @InputFiles
    val files: ListProperty<RegularFile> =
        this.project.objects.listProperty(RegularFile::class.java)

    @InputFiles
    val directories: ListProperty<Directory> =
        this.project.objects.listProperty(Directory::class.java)

    @Input
    val packageName: Property<String> =
        this.project.objects.property(String::class.java)

    @Input
    val classes: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    @Input
    val defaultScalarClass: Property<String?> =
        this.project.objects.property(String::class.java)

    @Input
    val nativeElements: SetProperty<String> =
        this.project.objects.setProperty(String::class.java)

    @Input
    val features: SetProperty<GenFeature> =
        this.project.objects.setProperty(GenFeature::class.java)

    @OutputDirectory
    val outputDirectory: DirectoryProperty =
        this.project.objects.directoryProperty()

    init {
        this.group = Specdsl.GROUP_NAME
        this.description = "Generate Kotlin DSL from Specdsl Schema"

        this.packageName.convention(SpecdslKotlin.DEFAULT_PACKAGE_NAME)
        this.classes.convention(SpecdslKotlin.DEFAULT_CLASSES)
        this.nativeElements.convention(SpecdslKotlin.DEFAULT_NATIVE_ELEMENTS)
        this.features.convention(emptySet())

        this.files.convention(emptyList())

        run {
            val directoriesProvider = this.project.provider {
                Specdsl.DEFAULT_DIRECTORIES.map { path ->
                    val fileProvider = this.project.provider { File(path) }
                    this.project.layout.dir(fileProvider).get()
                }
            }

            this.directories.convention(directoriesProvider)
        }
        run {
            val outputDirectoryProvider = run {
                val path = SpecdslKotlin.DEFAULT_OUTPUT_DIRECTORY
                this.project.layout.buildDirectory.dir(path)
            }

            this.outputDirectory.convention(outputDirectoryProvider)
        }
    }

    fun load(extension: SpecdslExtension) {
        if (!extension.kotlin.enabled) {
            this.enabled = false
            this.logger.warn("$TAG: task disabled")
            return
        }

        extension.files?.let { fileCollection ->
            val filesProvider = this.project.provider {
                fileCollection.files.map { file ->
                    val fileProvider = this.project.provider { file }
                    this.project.layout.file(fileProvider).get()
                }
            }

            this.files.convention(filesProvider)
        }
        extension.directories?.let { fileCollection ->
            val directoriesProvider = this.project.provider {
                fileCollection.files.map { file ->
                    val fileProvider = this.project.provider { file }
                    this.project.layout.dir(fileProvider).get()
                }
            }

            this.directories.convention(directoriesProvider)
        }

        this.packageName.set(extension.kotlin.packageName)
        this.classes.set(extension.kotlin.classes)
        this.defaultScalarClass.set(extension.kotlin.defaultScalarClass)
        this.nativeElements.set(extension.kotlin.nativeElements)
        this.features.set(extension.kotlin.features)

        extension.kotlin.outputDirectory?.also {
            this.outputDirectory.set(it)
        }
    }

    fun prepare() {
        val directory = this.outputDirectory.get().asFile
        directory.mkdirs()

        this.project.addToKotlinSourceSet(this, directory)
    }

    @TaskAction
    fun generate() {
        val filesGotten = this.files.get().mapTo(mutableListOf()) { it.asFile }
        val outputDirectoryGotten = this.outputDirectory.get().asFile

        // remove unfound files
        filesGotten.iterator().let { iter ->
            for (file in iter) {
                if (!file.isFile) {
                    this.logger.error("$TAG: Specified file does not exists: ${file.absolutePath}")
                    iter.remove()
                }
            }
        }

        // add automatically-scanned files from directories
        filesGotten += this.directories.get().flatMap {
            this.project.fileTree(it) { it.include("**.specdsl.json") }
                .filter { it.isFile }.files
        }

        var compactSpecSheet = CompactSpecSheet()

        for (file in filesGotten) {
            val source = try {
                file.readText()
            } catch (cause: IOException) {
                this.logger.error("$TAG: Couldn't read file: ${file.absolutePath}", cause)
                continue
            }

            compactSpecSheet += try {
                Json.decodeFromString<CompactSpecSheet>(source)
            } catch (cause: Exception) {
                this.logger.error("$TAG: Couldn't decode file: ${file.absolutePath}", cause)
                continue
            }
        }

        val specSheet = try {
            compactSpecSheet.inflate()
        } catch (e: Exception) {
            val message = "$TAG: schema inflation failure: ${e.message}"
            throw TaskInstantiationException(message, e)
        }

        val genContext = createGenContext(
            specSheet = specSheet,
            packageName = this.packageName.get(),
            classes = this.classes.get(),
            defaultScalarClass = this.defaultScalarClass.get(),
            nativeElements = this.nativeElements.get(),
            features = this.features.get()
        )

        try {
            runGenGroups(genContext)
        } catch (e: Exception) {
            val message = "$TAG: fetal code generation failure: ${e.message}"
            throw TaskInstantiationException(message, e)
        }

        genContext.failures.forEach { e ->
            val message = buildString {
                append("$TAG: ")
                append("${e.failure.group}: ")
                append("${e.failure.tag}: ")
                append(e.failure.message)
                append(" (element: ${signatureOf(e.failure.element)})")
            }

            logger.error(message, e)
        }

        val fileSpecSet = try {
            genContext.fileOptionalBlocks.forEach {
                it.value += {
                    addFileComment("This is an automatically generated file.\n")
                    addFileComment("Modification to this file WILL be lost everytime\n")
                    addFileComment("the code generation task is executed\n\n")
                    addFileComment("This file was generated with specdsl-gen-kotlin via\n")
                    addFileComment("gradle plugin: org.cufy.specdsl version: ${Specdsl.VERSION}\n")
                }
            }

            generateFileSpecSet(genContext)
        } catch (e: Exception) {
            val message = "$TAG: fetal code generation failure: ${e.message}"
            throw TaskInstantiationException(message, e)
        }

        for (fileSpec in fileSpecSet) {
            try {
                fileSpec.writeTo(outputDirectoryGotten)
            } catch (e: Exception) {
                val message = "$TAG: error while generating code: ${e.message}"
                logger.error(message, e)
            }
        }
    }
}
