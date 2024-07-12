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
     * Package name for generated DSL
     */
    var packageName: String =
        MMRPCKotlin.DEFAULT_PACKAGE_NAME

    var packaging: GenPackaging =
        MMRPCKotlin.DEFAULT_PACKAGING

    val classes: MutableMap<String, String> =
        MMRPCKotlin.DEFAULT_CLASSES.toMutableMap()

    var defaultScalarClass: String? =
        null

    val nativeElements: MutableSet<String> =
        MMRPCKotlin.DEFAULT_NATIVE_ELEMENTS.toMutableSet()

    val features: MutableSet<GenFeature> =
        mutableSetOf()

    /**
     * Output directory for generated DSL
     *
     * Default: `generated/sources/mmrpc/main/kotlin`
     */
    var outputDirectory: Directory? = null

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

    fun packingSubPackages() {
        packaging = GenPackaging.SUB_PACKAGES
    }
}
