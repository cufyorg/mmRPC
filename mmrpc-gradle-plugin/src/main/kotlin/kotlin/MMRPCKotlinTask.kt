package org.cufy.mmrpc.gradle.kotlin

import kotlinx.serialization.json.Json
import org.cufy.mmrpc.compact.CompactSpecSheet
import org.cufy.mmrpc.compact.inflate
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.generateFileSpecSet
import org.cufy.mmrpc.gen.kotlin.util.signatureOf
import org.cufy.mmrpc.gradle.MMRPC
import org.cufy.mmrpc.gradle.MMRPCExtension
import org.cufy.mmrpc.gradle.addToKotlinSourceSet
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

private const val TAG = "MMRPC_KOTLIN"

open class MMRPCKotlinTask : DefaultTask() {
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
    val packaging: Property<GenPackaging> =
        this.project.objects.property(GenPackaging::class.java)

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
        this.group = MMRPC.GROUP_NAME
        this.description = "Generate Kotlin DSL from mmRPC Schema"

        this.packageName.convention(MMRPCKotlin.DEFAULT_PACKAGE_NAME)
        this.packaging.convention(MMRPCKotlin.DEFAULT_PACKAGING)
        this.classes.convention(MMRPCKotlin.DEFAULT_CLASSES)
        this.nativeElements.convention(MMRPCKotlin.DEFAULT_NATIVE_ELEMENTS)
        this.features.convention(emptySet())

        this.files.convention(emptyList())

        run {
            val directoriesProvider = this.project.provider {
                MMRPC.DEFAULT_DIRECTORIES.map { path ->
                    val fileProvider = this.project.provider { File(path) }
                    this.project.layout.dir(fileProvider).get()
                }
            }

            this.directories.convention(directoriesProvider)
        }
        run {
            val outputDirectoryProvider = run {
                val path = MMRPCKotlin.DEFAULT_OUTPUT_DIRECTORY
                this.project.layout.buildDirectory.dir(path)
            }

            this.outputDirectory.convention(outputDirectoryProvider)
        }
    }

    fun load(extension: MMRPCExtension) {
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
        this.packaging.set(extension.kotlin.packaging)
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
            this.project.fileTree(it) { it.include("**.mmrpc.json") }
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
            packaging = this.packaging.get(),
            classes = this.classes.get(),
            defaultScalarClass = this.defaultScalarClass.get(),
            nativeElements = this.nativeElements.get(),
            features = this.features.get(),
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
            generateFileSpecSet(genContext) {
                addFileComment("This is an automatically generated file.\n")
                addFileComment("Modification to this file WILL be lost everytime\n")
                addFileComment("the code generation task is executed\n\n")
                addFileComment("This file was generated with mmrpc-gen-kotlin via\n")
                addFileComment("gradle plugin: org.cufy.mmrpc version: ${MMRPC.VERSION}\n")
            }
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
