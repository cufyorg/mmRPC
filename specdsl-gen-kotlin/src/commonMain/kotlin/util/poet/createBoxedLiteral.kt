package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createBoxedLiteral(element: Literal): CodeBlock {
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
    }
}
