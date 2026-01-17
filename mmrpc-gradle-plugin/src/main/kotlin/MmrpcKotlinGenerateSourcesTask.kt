package org.cufy.mmrpc.gradle

import com.squareup.kotlinpoet.ClassName
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.MmrpcSpec
import org.cufy.mmrpc.builtin
import org.cufy.mmrpc.collect
import org.cufy.mmrpc.compact.CompactElementDefinition
import org.cufy.mmrpc.compact.inflate
import org.cufy.mmrpc.experimental.fromJsonString
import org.cufy.mmrpc.experimental.fromYamlString
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.solve
import org.cufy.mmrpc.gradle.util.addToKotlinSourceSet
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
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
    val packageName: Property<String?> =
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
        this.features.convention(Mmrpc.Kotlin.DEFAULT_FEATURES)

        // names
        this.classNames.convention(Mmrpc.Kotlin.DEFAULT_CLASS_NAMES)

        // scalar classes
        this.defaultScalarClass.convention(Mmrpc.Kotlin.DEFAULT_DEFAULT_SCALAR_CLASS)
        this.scalarClasses.convention(Mmrpc.Kotlin.DEFAULT_SCALAR_CLASSES)

        // native classes
        this.nativeScalarClasses.convention(Mmrpc.Kotlin.DEFAULT_NATIVE_SCALAR_CLASSES)
        this.nativeMetadataClasses.convention(Mmrpc.Kotlin.DEFAULT_NATIVE_METADATA_CLASSES)

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
        this.features.set(extension.kotlin.features)

        // names
        this.classNames.set(extension.kotlin.classNames)

        // scalar classes
        this.defaultScalarClass.set(extension.kotlin.defaultScalarClass)
        this.scalarClasses.set(extension.kotlin.scalarClasses)

        // native classes
        this.nativeScalarClasses.set(extension.kotlin.nativeScalarClasses)
        this.nativeMetadataClasses.set(extension.kotlin.nativeMetadataClasses)

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
                    logger.error("$name: Specified file does not exists: ${file.absolutePath}")
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
                        MmrpcSpec.fromJsonString(source)
                    } catch (e: Exception) {
                        logger.error("$name: Couldn't decode file: ${file.absolutePath}", e)
                        continue
                    }

                    compactElements += spec.elements
                }

                "yaml", "yml" -> {
                    val spec = try {
                        MmrpcSpec.fromYamlString(source)
                    } catch (e: Exception) {
                        logger.error("$name: Couldn't decode file: ${file.absolutePath}", e)
                        continue
                    }

                    compactElements += spec.elements
                }

                else -> {
                    logger.error("$name: Unrecognized file extension ${file.absolutePath}")
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
            logger.error("$name: schema inflation failure: ${e.message}")
            return
        }

        val genContext = Context(
            elements = elements,
            packageName = this.packageName.get(),
            packaging = this.packaging.get(),
            features = this.features.get(),
            classNames = this.classNames.get().entries
                .associate { CanonicalName(it.key) to it.value },
            defaultScalarClass = this.defaultScalarClass.get()
                .let { ClassName.bestGuess(it) },
            scalarClasses = this.scalarClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            nativeScalarClasses = this.nativeScalarClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            nativeMetadataClasses = this.nativeMetadataClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            userdefinedScalarClasses = this.userdefinedScalarClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
            userdefinedMetadataClasses = this.userdefinedMetadataClasses.get().entries
                .associate { CanonicalName(it.key) to ClassName.bestGuess(it.value) },
        )

        val result = genContext.solve()

        result.fails.forEach { e ->
            val message = buildString {
                appendLine()
                append("mmRPC code-gen fail: ")
                e.refs.forEach {
                    appendLine()
                    append("- ")
                    append(it?.value)
                }
                appendLine()
            }

            logger.error(message, e)
        }

        for (fileSpec in result.files) {
            try {
                fileSpec.writeTo(outputDirectoryGotten)
            } catch (e: Exception) {
                logger.error("$name: error while generating code: ${e.message}", e)
            }
        }
    }
}
