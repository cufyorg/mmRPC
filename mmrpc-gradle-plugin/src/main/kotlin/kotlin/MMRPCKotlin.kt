package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.gen.kotlin.GenPackaging

object MMRPCKotlin {
    const val TASK_NAME = "generateMMRPCKotlinSources"
    const val DEFAULT_PACKAGE_NAME = ""
    val DEFAULT_PACKAGING = GenPackaging.SUB_PACKAGES

    val DEFAULT_CLASSES = mapOf(
        "builtin.Any" to "kotlin.Any",
        "builtin.String" to "kotlin.String",
        "builtin.Boolean" to "kotlin.Boolean",
        "builtin.Int32" to "kotlin.Int",
        "builtin.UInt32" to "kotlin.UInt",
        "builtin.Int64" to "kotlin.Long",
        "builtin.UInt64" to "kotlin.ULong",
        "builtin.Float32" to "kotlin.Float",
        "builtin.Float64" to "kotlin.Double",
        "builtin.Deprecated" to "kotlin.Deprecated",
    )

    val DEFAULT_NATIVE_ELEMENTS = setOf(
        "builtin.Any",
        "builtin.String",
        "builtin.Boolean",
        "builtin.Int32",
        "builtin.UInt32",
        "builtin.Int64",
        "builtin.UInt64",
        "builtin.Float32",
        "builtin.Float64",
        "builtin.Deprecated",

        "builtin.NULL",
        "builtin.TRUE",
        "builtin.FALSE",
    )

    const val DEFAULT_OUTPUT_DIRECTORY =
        "generated/sources/mmrpc/main/kotlin"
}
