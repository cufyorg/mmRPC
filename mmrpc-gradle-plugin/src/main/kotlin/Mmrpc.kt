package org.cufy.mmrpc.gradle

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging
import org.cufy.mmrpc.gen.kotlin.GenRange

object Mmrpc {
    const val VERSION = "0.0.1-experimental"
    const val GROUP_NAME = "mmrpc"
    const val EXTENSION_NAME = "mmrpc"

    val DEFAULT_DIRECTORIES = setOf(
        "src/main/resources/",
        "src/commonMain/resources/",
    )

    object Kotlin {
        const val GENERATE_SOURCES_TASK_NAME = "generateMmrpcKotlinSources"

        const val DEFAULT_OUTPUT_DIRECTORY =
            "generated/sources/mmrpc/main/kotlin"

        //

        const val DEFAULT_PACKAGE_NAME = ""
        val DEFAULT_PACKAGING = GenPackaging.SUB_PACKAGES
        val DEFAULT_RANGE = GenRange.EVERYTHING
        val DEFAULT_FEATURES = emptySet<GenFeature>()

        // names

        val DEFAULT_CLASS_NAMES = emptyMap<String, String>()

        // scalar classes

        const val DEFAULT_DEFAULT_SCALAR_CLASS = "kotlin.String"
        val DEFAULT_SCALAR_CLASSES = emptyMap<String, String>()

        // native classes

        val DEFAULT_NATIVE_SCALAR_CLASSES = mapOf(
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

        val DEFAULT_NATIVE_METADATA_CLASSES = mapOf(
            "builtin.Deprecated" to "kotlin.Deprecated",
        )

        val DEFAULT_NATIVE_CONSTANTS = setOf(
            "builtin.NULL",
            "builtin.TRUE",
            "builtin.FALSE",
        )

        // userdefined classes

        val DEFAULT_USERDEFINED_SCALAR_CLASSES = emptyMap<String, String>()
        val DEFAULT_USERDEFINED_METADATA_CLASSES = emptyMap<String, String>()
    }
}
