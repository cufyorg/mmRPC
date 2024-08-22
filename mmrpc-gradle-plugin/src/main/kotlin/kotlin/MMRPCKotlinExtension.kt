package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.gradle.api.file.Directory

class MMRPCKotlinExtension {
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
        MMRPCKotlin.Defaults.PACKAGE_NAME

    var packaging: GenPackaging =
        MMRPCKotlin.Defaults.PACKAGING

    val features: MutableSet<GenFeature> =
        MMRPCKotlin.Defaults.FEATURES.toMutableSet()

    // names

    val classNames: MutableMap<String, String> =
        MMRPCKotlin.Defaults.CLASS_NAMES.toMutableMap()

    // scalar classes

    var defaultScalarClass: String =
        MMRPCKotlin.Defaults.DEFAULT_SCALAR_CLASS

    val scalarClasses: MutableMap<String, String> =
        MMRPCKotlin.Defaults.SCALAR_CLASSES.toMutableMap()

    // native classes

    val nativeScalarClasses: MutableMap<String, String> =
        MMRPCKotlin.Defaults.NATIVE_SCALAR_CLASSES.toMutableMap()

    val nativeMetadataClasses: MutableMap<String, String> =
        MMRPCKotlin.Defaults.NATIVE_METADATA_CLASSES.toMutableMap()

    val nativeConstants: MutableSet<String> =
        MMRPCKotlin.Defaults.NATIVE_CONSTANTS.toMutableSet()

    // userdefined classes

    val customScalarClasses: MutableMap<String, String> =
        MMRPCKotlin.Defaults.USERDEFINED_SCALAR_CLASSES.toMutableMap()

    val customMetadataClasses: MutableMap<String, String> =
        MMRPCKotlin.Defaults.USERDEFINED_METADATA_CLASSES.toMutableMap()

    //

    fun kotlinxSerialization() {
        features += GenFeature.KOTLINX_SERIALIZATION
    }

    fun debug() {
        features += GenFeature.DEBUG
    }

    fun noBuiltin() {
        features += GenFeature.NO_BUILTIN
    }

    fun generateFieldObjects() {
        features += GenFeature.GEN_FIELD_OBJECTS
    }

    fun keepTypeClassNames() {
        features += GenFeature.KEEP_TYPE_CLASS_NAMES
    }

    fun keepFaultClassNames() {
        features += GenFeature.KEEP_FAULT_CLASS_NAMES
    }

    fun keepFieldPropertyNames() {
        features += GenFeature.KEEP_FIELD_PROPERTY_NAMES
    }

    fun packingSubPackages() {
        packaging = GenPackaging.SUB_PACKAGES
    }
}
