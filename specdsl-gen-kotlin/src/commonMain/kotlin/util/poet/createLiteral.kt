package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

private const val TAG = "createLiteral"

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [value]
 * in the given type [element].
 */
@Marker0
fun GenGroup.createLiteral(element: ConstDefinition): CodeBlock {
    return createLiteral(element.constType, element.constValue)
}

/**
 * Returns a code block that, when executed,
 * returns the representation of the given [value]
 * in the given type [element].
 */
@Marker0
fun GenGroup.createLiteral(element: TypeDefinition, value: Literal): CodeBlock {
    return when (element) {
        is ConstDefinition -> createLiteral(element.constType, value)
        is OptionalDefinition -> createLiteral(element.optionalType, value)
        is ArrayDefinition -> createLiteral(element, value)
        is ScalarDefinition -> createLiteral(element, value)

        is StructDefinition,
        is TupleDefinition,
        -> failGen(TAG, element) { "element not yet supported" }

        is InterDefinition,
        is UnionDefinition,
        -> failGen(TAG, element) { "element not supported" }
    }
}

private fun GenGroup.createLiteral(element: ArrayDefinition, value: Literal): CodeBlock {
    if (value !is TupleLiteral)
        failGen(TAG, element) { "illegal value: $value" }

    val items = value.value.joinToCode { createLiteral(element.arrayType, it) }
    return CodeBlock.of("listOf(%L)", items)
}

private fun GenGroup.createLiteral(element: ScalarDefinition, value: Literal): CodeBlock {
    val codeBlock = when (value) {
        is NullLiteral -> CodeBlock.of("%L", "null")
        is BooleanLiteral -> CodeBlock.of("%L", value.value)
        is IntLiteral -> CodeBlock.of("%L", value.value)
        is FloatLiteral -> CodeBlock.of("%L", value.value)
        is StringLiteral -> CodeBlock.of("%S", value.value)
        is TupleLiteral -> failGen(TAG, element) { "illegal value: $value" }
    }

    if (element.isAnonymous || element.canonicalName in ctx.nativeElements)
        return codeBlock

    return CodeBlock.of("%T(%L)", classOf(element), codeBlock)
}
