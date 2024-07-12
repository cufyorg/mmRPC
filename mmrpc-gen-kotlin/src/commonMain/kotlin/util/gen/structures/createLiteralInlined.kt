package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asEnumEntryName
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.poet.createCallSingleVararg

private const val TAG = "createLiteralInlined"

@Marker3
fun GenGroup.createLiteralInlined(element: ConstDefinition): CodeBlock {
    return createLiteralInlined(element.constType, element.constValue)
}

@Marker3
fun GenGroup.createLiteralInlined(element: TypeDefinition, value: Literal): CodeBlock {
    return when (element) {
        is OptionalDefinition -> createLiteralInlined(element.optionalType, value)
        is ArrayDefinition -> createLiteralInlined(element, value)
        is ScalarDefinition -> createLiteralInlined(element, value)
        is EnumDefinition -> createLiteralInlined(element, value)

        is StructDefinition -> failGen(TAG, element) { "element not supported" }
        is TupleDefinition -> failGen(TAG, element) { "element not supported" }
        is InterDefinition -> failGen(TAG, element) { "element not supported" }
        is UnionDefinition -> failGen(TAG, element) { "element not supported" }
    }
}

// ===================={    Literal    }==================== //

private fun GenGroup.createLiteralInlined(element: ScalarDefinition, value: Literal): CodeBlock {
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

private fun GenGroup.createLiteralInlined(element: EnumDefinition, literal: Literal): CodeBlock {
    if (!hasGeneratedClass(element))
        failGen(TAG, element) { "enums are required to have generated classes for this to work" }

    // find an entry with the same value presented
    val winner = element.enumEntries.firstOrNull { it.constValue == literal }
    winner ?: failGen(TAG, element) { "illegal value: $literal (enum entry not found)" }

    // create a reference to that entry
    return CodeBlock.of("%T.%L", generatedClassOf(element), winner.asEnumEntryName)
}

// ===================={ TupleLiteral  }==================== //

private fun GenGroup.createLiteralInlined(element: ArrayDefinition, literal: Literal): CodeBlock {
    if (literal !is TupleLiteral)
        failGen(TAG, element) { "illegal value: $literal" }

    return createCallSingleVararg(
        function = CodeBlock.of("arrayOf"),
        literal.value.map { createLiteral(element.arrayType, it) }
    )
}
