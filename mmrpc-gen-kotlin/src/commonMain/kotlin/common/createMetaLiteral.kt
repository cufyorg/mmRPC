package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.createCallSingleVararg

private const val TAG = "createMetadataLiteral.kt"

/**
 * Returns a code block that, when executed,
 * returns the kotlin-annotation-compatible
 * representation of the given [element].
 *
 * > Remember: annotations does not support null values.
 */
@Marker3
fun GenScope.createMetaLiteral(element: ConstDefinition): CodeBlock {
    return createMetaLiteral(element.type, element.value)
}

/**
 * Returns a code block that, when executed,
 * returns the kotlin-annotation-compatible
 * representation of the given [element].
 *
 * > Remember: annotations does not support null values.
 */
@Marker3
fun GenScope.createMetaLiteral(element: TypeDefinition, literal: Literal): CodeBlock {
    return when (element) {
        is ArrayDefinition -> when (literal) {
            is TupleLiteral -> createMetadataLiteralOfArray(element, literal)
            else -> fail(TAG, element) { "illegal value: $literal" }
        }

        is ScalarDefinition -> createMetadataLiteralOfScalar(element, literal)
        is EnumDefinition -> createMetadataLiteralOfEnum(element, literal)

        is OptionalDefinition -> fail(TAG, element) { "element not supported" }
        is StructDefinition -> fail(TAG, element) { "element not supported" }
        is TupleDefinition -> fail(TAG, element) { "element not supported" }
        is InterDefinition -> fail(TAG, element) { "element not supported" }
        is UnionDefinition -> fail(TAG, element) { "element not supported" }
    }
}

// ===================={    Literal    }==================== //

private fun GenScope.createMetadataLiteralOfScalar(element: ScalarDefinition, value: Literal): CodeBlock {
    return when (value) {
        is NullLiteral -> fail(TAG, element) { "illegal value: $value" }
        is BooleanLiteral -> CodeBlock.of("%L", value.value)
        is IntLiteral -> CodeBlock.of("%L", value.value)
        is FloatLiteral -> CodeBlock.of("%L", value.value)
        is StringLiteral -> CodeBlock.of("%S", value.value)
        is TupleLiteral -> fail(TAG, element) { "illegal value: $value" }
        is StructLiteral -> fail(TAG, element) { "illegal value: $value" }
    }
}

private fun GenScope.createMetadataLiteralOfEnum(element: EnumDefinition, literal: Literal): CodeBlock {
    // find an entry with the same value presented
    val winner = element.entries.firstOrNull { it.value == literal }
    winner ?: fail(TAG, element) { "illegal value: $literal (enum entry not found)" }

    // create a reference to that entry
    return CodeBlock.of("%T.%L", generatedClassOf(element.canonicalName), asEnumEntryName(winner))
}

// ===================={ TupleLiteral  }==================== //

private fun GenScope.createMetadataLiteralOfArray(element: ArrayDefinition, literal: TupleLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("arrayOf"),
        literal.value.map { createMetaLiteral(element.type, it) }
    )
}
