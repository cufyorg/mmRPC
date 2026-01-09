package org.cufy.mmrpc.gen.kotlin.common

import org.cufy.mmrpc.*

private const val TAG = "serialName.kt"

@Marker3
fun TypeDefinition.typeSerialName(): String {
    return when (this) {
        is EnumDefinition,
        is UnionDefinition,
        is InterDefinition,
        is ScalarDefinition,
        is StructDefinition,
        is TupleDefinition,
        -> canonicalName.value

        is ArrayDefinition,
        is MapDefinition,
        is OptionalDefinition,
        -> fail(TAG, this) { "Cannot produce serial name for element" }
    }
}

@Marker3
fun ConstDefinition.enumEntrySerialName(): String {
    return when (val literal = value) {
        is StringLiteral -> literal.value
        else -> "\"${literal.contentToString()}\""
    }
}

@Marker3
fun FieldDefinition.propertySerialName(): String {
    return key ?: name
}
