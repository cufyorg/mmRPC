package org.cufy.mmrpc.gradle

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.GenRange
import org.gradle.api.Action
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection

open class MmrpcExtension {
    /**
     * SpecSheet files.
     */
    var files: FileCollection? = null

    /**
     * Input directories.
     *
     * Supported Extensions:
     * - **.mmrpc.json
     * - **.mmrpc.yaml
     * - **.mmrpc.yml
     *
     * Default: `["src/main/resources/", "src/commonMain/resources/"]`
     */
    var directories: FileCollection? = null

    val kotlin by lazy { Kotlin() }
    fun kotlin(action: Action<Kotlin>) = action.execute(kotlin)

    class Kotlin {
        /**
         * Is Kotlin DSL generation enabled
         *
         * Default: true
         */
        var enabled: Boolean = true

        /**
         * Output directory for generated DSL
         *
         * Default: `generated/sources/mmrpc/main/kotlin`
         */
        var outputDirectory: Directory? = null

        //

        /**
         * Package name for generated DSL
         */
        var packageName: String =
            Mmrpc.Kotlin.DEFAULT_PACKAGE_NAME

        var packaging: GenPackaging =
            Mmrpc.Kotlin.DEFAULT_PACKAGING

        var range: GenRange =
            Mmrpc.Kotlin.DEFAULT_RANGE

        val features: MutableSet<GenFeature> =
            Mmrpc.Kotlin.DEFAULT_FEATURES.toMutableSet()

        // names

        val classNames: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_CLASS_NAMES.toMutableMap()

        val protocolSuffix: String =
            Mmrpc.Kotlin.DEFAULT_PROTOCOL_SUFFIX

        // scalar classes

        var defaultScalarClass: String =
            Mmrpc.Kotlin.DEFAULT_DEFAULT_SCALAR_CLASS

        val scalarClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_SCALAR_CLASSES.toMutableMap()

        // native classes

        val nativeScalarClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_NATIVE_SCALAR_CLASSES.toMutableMap()

        val nativeMetadataClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_NATIVE_METADATA_CLASSES.toMutableMap()

        val nativeConstants: MutableSet<String> =
            Mmrpc.Kotlin.DEFAULT_NATIVE_CONSTANTS.toMutableSet()

        // userdefined classes

        val customScalarClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_USERDEFINED_SCALAR_CLASSES.toMutableMap()

        val customMetadataClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_USERDEFINED_METADATA_CLASSES.toMutableMap()

        // @formatter:off
        fun debug() { features += GenFeature.DEBUG }
        fun kotlinxSerialization() { features += GenFeature.KOTLINX_SERIALIZATION }

        fun keepTypeClassNames() { features += GenFeature.KEEP_TYPE_CLASS_NAMES }
        fun keepFaultClassNames() { features += GenFeature.KEEP_FAULT_CLASS_NAMES }
        fun keepFieldPropertyNames() { features += GenFeature.KEEP_FIELD_PROPERTY_NAMES }

        fun packingSubPackages() { packaging = GenPackaging.SUB_PACKAGES }

        fun generateFieldNameProperties() { features += GenFeature.GEN_FIELD_NAME_PROPERTIES }
        fun generateConstValueProperties() { features += GenFeature.GEN_CONST_VALUE_PROPERTIES }

        fun generateEverything() { range = GenRange.EVERYTHING }
        fun generateSharedOnly() { range = GenRange.SHARED_ONLY }
        fun generateCommOnly() { range = GenRange.COMM_ONLY }
        // @formatter:on
    }
}
