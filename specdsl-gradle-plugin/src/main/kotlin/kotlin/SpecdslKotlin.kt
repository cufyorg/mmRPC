package org.cufy.specdsl.gradle.kotlin

object SpecdslKotlin {
    const val TASK_NAME = "generateSpecdslKotlinSources"
    const val DEFAULT_PACKAGE_NAME = "specdsl"

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
        "generated/sources/specdsl/main/kotlin"
}
