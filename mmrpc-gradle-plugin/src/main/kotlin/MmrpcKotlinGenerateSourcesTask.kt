package org.cufy.mmrpc.gradle

import com.charleskorn.kaml.Yaml
import com.squareup.kotlinpoet.ClassName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.MmrpcSpec
import org.cufy.mmrpc.builtin
import org.cufy.mmrpc.collect
import org.cufy.mmrpc.compact.CompactElementDefinition
import org.cufy.mmrpc.compact.inflate
import org.cufy.mmrpc.gen.kotlin.*
import org.cufy.mmrpc.gen.kotlin.common.humanSignature
import org.cufy.mmrpc.gen.kotlin.gen.*
import org.cufy.mmrpc.gradle.util.addToKotlinSourceSet
import org.cufy.mmrpc.gradle.util.collectIgnored
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

open class MmrpcKotlinGenerateSourcesTask : DefaultTask() {
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
    val range: Property<GenRange> =
        this.project.objects.property(GenRange::class.java)

    @Input
    val features: SetProperty<GenFeature> =
        this.project.objects.setProperty(GenFeature::class.java)

    // names

    @Input
    val classNames: MapProperty<String, String> =
        this.project.objects.mapProperty(String::class.java, String::class.java)

    @Input
    val protocolSuffix: Property<String> =
        this.project.objects.property(String::class.java)

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
        this.group = Mmrpc.GROUP_NAME
        this.description = "Generate Kotlin DSL from mmRPC Schema"

        this.files.convention(emptyList())
        this.directories.convention(this.project.provider {
            Mmrpc.DEFAULT_DIRECTORIES.map { path ->
                val fileProvider = this.project.provider { File(path) }

                this.project.layout.dir(fileProvider).get()
            }
        })
        this.outputDirectory.convention(run {
            val path = Mmrpc.Kotlin.DEFAULT_OUTPUT_DIRECTORY

            this.project.layout.buildDirectory.dir(path)
        })

        //
        this.packageName.convention(Mmrpc.Kotlin.DEFAULT_PACKAGE_NAME)
        this.packaging.convention(Mmrpc.Kotlin.DEFAULT_PACKAGING)
        this.range.convention(Mmrpc.Kotlin.DEFAULT_RANGE)
        this.features.convention(Mmrpc.Kotlin.DEFAULT_FEATURES)

        // names
        this.classNames.convention(Mmrpc.Kotlin.DEFAULT_CLASS_NAMES)
        this.protocolSuffix.convention(Mmrpc.Kotlin.DEFAULT_PROTOCOL_SUFFIX)

        // scalar classes
        this.defaultScalarClass.convention(Mmrpc.Kotlin.DEFAULT_DEFAULT_SCALAR_CLASS)
        this.scalarClasses.convention(Mmrpc.Kotlin.DEFAULT_SCALAR_CLASSES)

        // native classes
        this.nativeScalarClasses.convention(Mmrpc.Kotlin.DEFAULT_NATIVE_SCALAR_CLASSES)
        this.nativeMetadataClasses.convention(Mmrpc.Kotlin.DEFAULT_NATIVE_METADATA_CLASSES)
        this.nativeConstants.convention(Mmrpc.Kotlin.DEFAULT_NATIVE_CONSTANTS)

        // userdefined classes
        this.userdefinedScalarClasses.convention(Mmrpc.Kotlin.DEFAULT_USERDEFINED_SCALAR_CLASSES)
        this.userdefinedMetadataClasses.convention(Mmrpc.Kotlin.DEFAULT_USERDEFINED_METADATA_CLASSES)
    }

    fun load(extension: MmrpcExtension) {
        if (!extension.kotlin.enabled) {
            this.enabled = false
            this.logger.warn("$name: task disabled")
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
        this.range.set(extension.kotlin.range)
        this.features.set(extension.kotlin.features)

        // names
        this.classNames.set(extension.kotlin.classNames)
        this.protocolSuffix.set(extension.kotlin.protocolSuffix)

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
                    this.logger.error("$name: Specified file does not exists: ${file.absolutePath}")
                    iter.remove()
                }
            }
        }

        // add automatically-scanned files from directories
        filesGotten += this.directories.get().flatMap {
            this.project.fileTree(it) {
                it.include("**.mmrpc.json")
                it.include("**.mmrpc.yaml")
                it.include("**.mmrpc.yml")
            }.filter { it.isFile }.files
        }

        val compactElements = mutableListOf<CompactElementDefinition>()

        for (file in filesGotten) {
            val source = try {
                file.readText()
            } catch (cause: IOException) {
                this.logger.error("$name: Couldn't read file: ${file.absolutePath}", cause)
                continue
            }

            when (file.extension) {
                "json" -> {
                    val spec = try {
                        Json.decodeFromString<MmrpcSpec>(source)
                    } catch (cause: Exception) {
                        this.logger.error("$name: Couldn't decode file: ${file.absolutePath}", cause)
                        continue
                    }

                    compactElements += spec.elements
                }

                "yaml", "yml" -> {
                    val spec = try {
                        Yaml.default.decodeFromString<MmrpcSpec>(source)
                    } catch (cause: Exception) {
                        this.logger.error("$name: Couldn't decode file: ${file.absolutePath}", cause)
                        continue
                    }

                    compactElements += spec.elements
                }

                else -> {
                    this.logger.error("$name: Unrecognized file extension ${file.absolutePath}")
                }
            }
        }

        val elements = try {
            compactElements.asSequence()
                .inflate(builtin.elements)
                .flatMap { it.collect() }
                .distinctBy { it.canonicalName }
                .toList()
        } catch (e: Exception) {
            val message = "$name: schema inflation failure: ${e.message}"
            throw TaskInstantiationException(message, e)
        }

        val genContext = GenContext(
            elements = elements,
            ignore = collectIgnored(
                elements = elements,
                includeRange = this.range.get(),
            ),
            //
            packageName = this.packageName.get(),
            packaging = this.packaging.get(),
            features = this.features.get(),
            // names
            classNames = this.classNames.get().entries
                .associate { CanonicalName(it.key) to it.value },
            protocolSuffix = this.protocolSuffix.get(),
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
            genContext.run {
                consumeArrayDefinition()
                consumeConstDefinition()
                consumeEnumDefinition()
                consumeFaultDefinition()
                consumeProtocolDefinition()
                consumeRoutineDefinition()
                consumeFieldDefinition()
                consumeInterDefinition()
                consumeMetadataDefinition()
                consumeScalarDefinition()
                consumeStructDefinition()
                consumeTupleDefinition()
                consumeUnionDefinition()
            }
        } catch (e: Exception) {
            val message = "$name: fetal code generation failure: ${e.message}"
            throw TaskInstantiationException(message, e)
        }

        genContext.failures.forEach { e ->
            val message = buildString {
                append("$name: ")
                append("${e.failure.tag}: ")
                append(e.failure.message)
                append(" (element: ${e.failure.element?.let { it.humanSignature() }})")
            }

            logger.error(message, e)
        }

        val fileSpecSet = try {
            genContext.run {
                generateFileSpecSet {
                    addFileComment("This is an automatically generated file.\n")
                    addFileComment("Modification to this file WILL be lost everytime\n")
                    addFileComment("the code generation task is executed\n\n")
                    addFileComment("This file was generated with mmrpc-gen-kotlin via\n")
                    addFileComment("gradle plugin: org.cufy.mmrpc version: ${Mmrpc.VERSION}\n")
                }
            }
        } catch (e: Exception) {
            val message = "$name: fetal code generation failure: ${e.message}"
            throw TaskInstantiationException(message, e)
        }

        for (fileSpec in fileSpecSet) {
            try {
                fileSpec.writeTo(outputDirectoryGotten)
            } catch (e: Exception) {
                val message = "$name: error while generating code: ${e.message}"
                logger.error(message, e)
            }
        }
    }
}
