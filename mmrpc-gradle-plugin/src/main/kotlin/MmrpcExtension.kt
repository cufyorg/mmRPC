package org.cufy.mmrpc.gradle

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
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
        var packageName: String? =
            Mmrpc.Kotlin.DEFAULT_PACKAGE_NAME

        var packaging: GenPackaging =
            Mmrpc.Kotlin.DEFAULT_PACKAGING

        val features: MutableSet<GenFeature> =
            Mmrpc.Kotlin.DEFAULT_FEATURES.toMutableSet()

        // names

        val classNames: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_CLASS_NAMES.toMutableMap()

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

        // userdefined classes

        val customScalarClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_USERDEFINED_SCALAR_CLASSES.toMutableMap()

        val customMetadataClasses: MutableMap<String, String> =
            Mmrpc.Kotlin.DEFAULT_USERDEFINED_METADATA_CLASSES.toMutableMap()

        // @formatter:off
        fun debug() { features += GenFeature.DEBUG }

        fun generateTypes() { features += GenFeature.GENERATE_TYPES }
        fun generateProtocols() { features += GenFeature.GENERATE_PROTOCOLS }

        fun keepTypeClassNames() { features += GenFeature.KEEP_TYPE_CLASS_NAMES }
        fun keepFaultClassNames() { features += GenFeature.KEEP_FAULT_CLASS_NAMES }
        fun keepProtocolClassNames() { features += GenFeature.KEEP_PROTOCOL_CLASS_NAMES }
        fun keepRoutineClassNames() { features += GenFeature.KEEP_ROUTINE_CLASS_NAMES }
        fun keepRoutineFunctionNames() { features += GenFeature.KEEP_ROUTINE_FUNCTION_NAMES }
        fun keepFieldPropertyNames() { features += GenFeature.KEEP_FIELD_PROPERTY_NAMES }
        fun keepConstPropertyNames() { features += GenFeature.KEEP_CONST_PROPERTY_NAMES }
        fun keepEnumEntryNames() { features += GenFeature.KEEP_ENUM_ENTRY_NAMES }

        fun packingSubPackages() { packaging = GenPackaging.SUB_PACKAGES }
        // @formatter:on
    }
}
