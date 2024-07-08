package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.gradle.api.file.Directory
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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

    val classes: MutableMap<String, String> =
        MMRPCKotlin.DEFAULT_CLASSES.toMutableMap()

    var defaultScalarClass: String? =
        null

    val nativeElements: MutableSet<String> =
        MMRPCKotlin.DEFAULT_NATIVE_ELEMENTS.toMutableSet()

    val features: MutableSet<GenFeature> =
        mutableSetOf()

    var featureKotlinxSerialization by feature(
        GenFeature.KOTLINX_SERIALIZATION
    )

    var featureDebug by feature(
        GenFeature.DEBUG
    )

    /**
     * Output directory for generated DSL
     *
     * Default: `generated/sources/mmrpc/main/kotlin`
     */
    var outputDirectory: Directory? = null

    //

    private fun feature(feature: GenFeature) = object : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            feature in features

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) =
            if (value) features += feature else features -= feature
    }
}
