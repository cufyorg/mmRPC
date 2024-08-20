package org.cufy.mmrpc.gradle.kotlin

import com.squareup.kotlinpoet.ClassName
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.compact.CompactSpecSheet
import org.cufy.mmrpc.compact.inflate
import org.cufy.mmrpc.gen.kotlin.GenContext
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

    @OutputDirectory
    val outputDirectory: DirectoryProperty =
        this.project.objects.directoryProperty()

    //

    @Input
    val packageName: Property<String> =
        this.project.objects.property(String::class.java)

    @Input
    val packaging: Property<GenPackaging> =
        this.project.objects.property(GenPackaging::class.java)

    @Input
    val features: SetProperty<GenFeature> =
        this.project.objects.setProperty(GenFeature::class.java)

    // names

    @Input
    val classNames: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    // scalar classes

    @Input
    val defaultScalarClass: Property<String> =
        this.project.objects.property(String::class.java)

    @Input
    val scalarClasses: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    // native classes

    @Input
    val nativeScalarClasses: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    @Input
    val nativeMetadataClasses: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    @Input
    val nativeConstants: SetProperty<String> =
        this.project.objects.setProperty(String::class.java)

    // userdefined classes

    @Input
    val userdefinedScalarClasses: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    @Input
    val userdefinedMetadataClasses: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    init {
        this.group = MMRPC.GROUP_NAME
        this.description = "Generate Kotlin DSL from mmRPC Schema"

        this.files.convention(emptyList())
        this.directories.convention(this.project.provider {
            MMRPC.Defaults.DIRECTORIES.map { path ->
                val fileProvider = this.project.provider { File(path) }

                this.project.layout.dir(fileProvider).get()
            }
        })
        this.outputDirectory.convention(run {
            val path = MMRPCKotlin.Defaults.OUTPUT_DIRECTORY

            this.project.layout.buildDirectory.dir(path)
        })

        //
        this.packageName.convention(MMRPCKotlin.Defaults.PACKAGE_NAME)
        this.packaging.convention(MMRPCKotlin.Defaults.PACKAGING)
        this.features.convention(MMRPCKotlin.Defaults.FEATURES)

        // names
        this.classNames.convention(MMRPCKotlin.Defaults.CLASS_NAMES)

        // scalar classes
        this.defaultScalarClass.convention(MMRPCKotlin.Defaults.DEFAULT_SCALAR_CLASS)
        this.scalarClasses.convention(MMRPCKotlin.Defaults.SCALAR_CLASSES)

        // native classes
        this.nativeScalarClasses.convention(MMRPCKotlin.Defaults.NATIVE_SCALAR_CLASSES)
        this.nativeMetadataClasses.convention(MMRPCKotlin.Defaults.NATIVE_METADATA_CLASSES)
        this.nativeConstants.convention(MMRPCKotlin.Defaults.NATIVE_CONSTANTS)

        // userdefined classes
        this.userdefinedScalarClasses.convention(MMRPCKotlin.Defaults.USERDEFINED_SCALAR_CLASSES)
        this.userdefinedMetadataClasses.convention(MMRPCKotlin.Defaults.USERDEFINED_METADATA_CLASSES)
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
        extension.kotlin.outputDirectory?.also {
            this.outputDirectory.set(it)
        }

        //
        this.packageName.set(extension.kotlin.packageName)
        this.packaging.set(extension.kotlin.packaging)
        this.features.set(extension.kotlin.features)

        // names
        this.classNames.set(extension.kotlin.classNames)

        // scalar classes
        this.defaultScalarClass.set(extension.kotlin.defaultScalarClass)
        this.scalarClasses.set(extension.kotlin.scalarClasses)

        // native classes
        this.nativeScalarClasses.set(extension.kotlin.nativeScalarClasses)
        this.nativeMetadataClasses.set(extension.kotlin.nativeMetadataClasses)
        this.nativeConstants.set(extension.kotlin.nativeConstants)

        // userdefined classes
        this.userdefinedScalarClasses.set(extension.kotlin.customScalarClasses)
        this.userdefinedMetadataClasses.set(extension.kotlin.customMetadataClasses)
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

        val genContext = GenContext(
            specSheet = specSheet,
            //
            packageName = this.packageName.get(),
            packaging = this.packaging.get(),
            features = this.features.get(),
            // names
            classNames = this.classNames.get().entries
                .associate { CanonicalName(it.key) to it.value },
            // scalar classes
            defaultScalarClass = this.defaultScalarClass.get()
                .let { ClassName.bestGuess(it) },
            scalarClasses = this.scalarClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            // native classes
            nativeScalarClasses = this.nativeScalarClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            nativeMetadataClasses = this.nativeMetadataClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            nativeConstants = this.nativeConstants.get()
                .map { CanonicalName(it) }
                .toSet(),
            // userdefined classes
            userdefinedScalarClasses = this.userdefinedScalarClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            userdefinedMetadataClasses = this.userdefinedMetadataClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
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
