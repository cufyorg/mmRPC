package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*

/**
 * Return code that, when executed, returns the given literal [element].
 */
fun createBoxedLiteral(element: Literal): CodeBlock {
    return when (element) {
        is NullLiteral
        -> CodeBlock.of("%T", NullLiteral::class)

        is BooleanLiteral
        -> CodeBlock.of("%T(%L)", BooleanLiteral::class, element.value)

        is IntLiteral
        -> CodeBlock.of("%T(%L)", IntLiteral::class, element.value)

        is FloatLiteral
        -> CodeBlock.of("%T(%L)", FloatLiteral::class, element.value)

        is StringLiteral
        -> CodeBlock.of("%T(%S)", StringLiteral::class, element.value)

        is TupleLiteral
        -> {
            createCallSingleVararg(
                function = CodeBlock.of("%T", TupleLiteral::class),
                element.value.map { createBoxedLiteral(it) }
            )
        }

        is StructLiteral
        -> {
            createCallSingleVararg(
                function = CodeBlock.of("%T", StructLiteral::class),
                element.value.map { CodeBlock.of("%S to %L", it.key, createBoxedLiteral(it.value)) }
            )
        }
    }
}
