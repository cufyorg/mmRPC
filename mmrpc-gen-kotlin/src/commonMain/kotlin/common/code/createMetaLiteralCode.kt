package org.cufy.mmrpc.gen.kotlin.common.code

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.common.model.generatedClassName
import org.cufy.mmrpc.gen.kotlin.common.model.nameOfEnumEntry
import org.cufy.mmrpc.gen.kotlin.context.Context
import org.cufy.mmrpc.gen.kotlin.context.fail
import org.cufy.mmrpc.gen.kotlin.util.createCallSingleVararg

/**
 * Returns a code block that, when executed,
 * returns the kotlin-annotation-compatible
 * representation of the given [element].
 *
 * > Remember: annotations does not support null values.
 */
@ContextScope
context(ctx: Context)
fun createMetaLiteralCode(element: TypeDefinition, literal: Literal): CodeBlock {
    return when (element) {
        is ArrayDefinition -> when (literal) {
            is TupleLiteral -> createMetaLiteralCodeOfArray(element, literal)
            else -> fail(element, "illegal value: $literal")
        }

        is ScalarDefinition -> createMetaLiteralCodeOfScalar(element, literal)
        is EnumDefinition -> createMetaLiteralCodeOfEnum(element, literal)

        is MapDefinition,
        is OptionalDefinition,
        is StructDefinition,
        is TupleDefinition,
        is UnionDefinition,
        is TraitDefinition,
        -> fail(element, "element not supported")
    }
}

// ===================={    Literal    }==================== //

@ContextScope
context(ctx: Context)
private fun createMetaLiteralCodeOfScalar(element: ScalarDefinition, value: Literal): CodeBlock {
    return when (value) {
        is NullLiteral -> fail(element, "illegal value: $value")

        is BooleanLiteral -> CodeBlock.of("%L", value.value)
        is IntLiteral -> CodeBlock.of("%L", value.value)
        is FloatLiteral -> CodeBlock.of("%L", value.value)
        is StringLiteral -> CodeBlock.of("%S", value.value)
        is TupleLiteral -> fail(element, "illegal value: $value")

        is StructLiteral -> fail(element, "illegal value: $value")
    }
}

@ContextScope
context(ctx: Context)
private fun createMetaLiteralCodeOfEnum(element: EnumDefinition, literal: Literal): CodeBlock {
    // find an entry with the same value presented
    val winner = element.entries.firstOrNull { it.value == literal }
    winner ?: fail(element, "illegal value: $literal (enum entry not found)")

    // create a reference to that entry
    return CodeBlock.of("%T.%L", element.generatedClassName(), winner.nameOfEnumEntry())
}

// ===================={ TupleLiteral  }==================== //

@ContextScope
context(ctx: Context)
private fun createMetaLiteralCodeOfArray(element: ArrayDefinition, literal: TupleLiteral): CodeBlock {
    return createCallSingleVararg(
        function = CodeBlock.of("arrayOf"),
        literal.value.map { createMetaLiteralCode(element.type, it) }
    )
}
