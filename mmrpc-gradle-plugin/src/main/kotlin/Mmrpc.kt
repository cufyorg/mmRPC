package org.cufy.mmrpc.gradle

import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenPackaging

object Mmrpc {
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

        const val DEFAULT_PACKAGE_NAME: String = ""
        val DEFAULT_PACKAGING = GenPackaging.SUB_PACKAGES
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
            "builtin.Byte" to "kotlin.Byte",
            "builtin.ByteArray" to "kotlin.ByteArray",
            "builtin.Int32" to "kotlin.Int",
            "builtin.Int32Array" to "kotlin.IntArray",
            "builtin.UInt32" to "kotlin.UInt",
            "builtin.UInt32Array" to "kotlin.UIntArray",
            "builtin.Int64" to "kotlin.Long",
            "builtin.Int64Array" to "kotlin.LongArray",
            "builtin.UInt64" to "kotlin.ULong",
            "builtin.UInt64Array" to "kotlin.ULongArray",
            "builtin.Float32" to "kotlin.Float",
            "builtin.Float32Array" to "kotlin.FloatArray",
            "builtin.Float64" to "kotlin.Double",
            "builtin.Float64Array" to "kotlin.DoubleArray",
        )

        val DEFAULT_NATIVE_METADATA_CLASSES = mapOf(
            "builtin.Deprecated" to "kotlin.Deprecated",
        )

        // userdefined classes

        val DEFAULT_USERDEFINED_SCALAR_CLASSES = emptyMap<String, String>()
        val DEFAULT_USERDEFINED_METADATA_CLASSES = emptyMap<String, String>()
    }
}
