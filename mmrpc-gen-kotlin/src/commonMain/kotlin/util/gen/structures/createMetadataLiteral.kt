package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.gen.references.asEnumEntryName
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

private const val TAG = "createMetadataLiteral"

/**
 * Returns a code block that, when executed,
 * returns the kotlin-annotation-compatible
 * representation of the given [element].
 *
 * > Remember: annotations does not support null values.
 */
@Marker3
fun GenGroup.createMetadataLiteral(element: ConstDefinition): CodeBlock {
    return createMetadataLiteral(element.constType, element.constValue)
}

/**
 * Returns a code block that, when executed,
 * returns the kotlin-annotation-compatible
 * representation of the given [element].
 *
 * > Remember: annotations does not support null values.
 */
@Marker3
fun GenGroup.createMetadataLiteral(element: TypeDefinition, literal: Literal): CodeBlock {
    return when (element) {
        is ArrayDefinition -> when (literal) {
            is TupleLiteral -> createMetadataLiteralOfArray(element, literal)
            else -> failGen(TAG, element) { "illegal value: $literal" }
        }

        is ScalarDefinition -> createMetadataLiteralOfScalar(element, literal)
        is EnumDefinition -> createMetadataLiteralOfEnum(element, literal)

        is OptionalDefinition -> failGen(TAG, element) { "element not supported" }
        is StructDefinition -> failGen(TAG, element) { "element not supported" }
        is TupleDefinition -> failGen(TAG, element) { "element not supported" }
        is InterDefinition -> failGen(TAG, element) { "element not supported" }
        is UnionDefinition -> failGen(TAG, element) { "element not supported" }
    }
}

// ===================={    Literal    }==================== //

private fun GenGroup.createMetadataLiteralOfScalar(element: ScalarDefinition, value: Literal): CodeBlock {
    return when (value) {
        is NullLiteral -> failGen(TAG, element) { "illegal value: $value" }
        is BooleanLiteral -> CodeBlock.of("%L", value.value)
        is IntLiteral -> CodeBlock.of("%L", value.value)
        is FloatLiteral -> CodeBlock.of("%L", value.value)
        is StringLiteral -> CodeBlock.of("%S", value.value)
        is TupleLiteral -> failGen(TAG, element) { "illegal value: $value" }
        is StructLiteral -> failGen(TAG, element) { "illegal value: $value" }
    }
}

private fun GenGroup.createMetadataLiteralOfEnum(element: EnumDefinition, literal: Literal): CodeBlock {
    // find an entry with the same value presented
    val winner = element.enumEntries.firstOrNull { it.constValue == literal }
    winner ?: failGen(TAG, element) { "illegal value: $literal (enum entry not found)" }

    // create a reference to that entry
    return CodeBlock.of("%T.%L", generatedClassOf(element), asEnumEntryName(winner))
}

// ===================={ TupleLiteral  }==================== //

private fun GenGroup.createMetadataLiteralOfArray(element: ArrayDefinition, literal: TupleLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("arrayOf"),
        literal.value.map { createMetadataLiteral(element.arrayType, it) }
    )
}
