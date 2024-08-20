package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging

object MMRPCKotlin {
    const val TASK_NAME = "generateMMRPCKotlinSources"

    object Defaults {
        const val OUTPUT_DIRECTORY =
            "generated/sources/mmrpc/main/kotlin"

        //

        const val PACKAGE_NAME = ""
        val PACKAGING = GenPackaging.SUB_PACKAGES
        val FEATURES = emptySet<GenFeature>()

        // names

        val CLASS_NAMES = emptyMap<String, String>()

        // scalar classes

        const val DEFAULT_SCALAR_CLASS = "kotlin.String"
        val SCALAR_CLASSES = emptyMap<String, String>()

        // native classes

        val NATIVE_SCALAR_CLASSES = mapOf(
            "builtin.Any" to "kotlin.Any",
            "builtin.String" to "kotlin.String",
            "builtin.Boolean" to "kotlin.Boolean",
            "builtin.Int32" to "kotlin.Int",
            "builtin.UInt32" to "kotlin.UInt",
            "builtin.Int64" to "kotlin.Long",
            "builtin.UInt64" to "kotlin.ULong",
            "builtin.Float32" to "kotlin.Float",
            "builtin.Float64" to "kotlin.Double",
        )

        val NATIVE_METADATA_CLASSES = mapOf(
            "builtin.Deprecated" to "kotlin.Deprecated",
        )

        val NATIVE_CONSTANTS = setOf(
            "builtin.NULL",
            "builtin.TRUE",
            "builtin.FALSE",
        )

        // userdefined classes

        val USERDEFINED_SCALAR_CLASSES = emptyMap<String, String>()
        val USERDEFINED_METADATA_CLASSES = emptyMap<String, String>()
    }
}
