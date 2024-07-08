package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode

fun createCall(function: CodeBlock, vararg parameters: Pair<String, CodeBlock>): CodeBlock {
    return createCall(
        function = function,
        parameters = parameters.toMap()
    )
}

fun createCall(function: CodeBlock, parameters: Map<String, CodeBlock>): CodeBlock {
    if (parameters.isEmpty())
        return CodeBlock.of("%L()", function)

    return CodeBlock.of(
        format = "%L(\n⇥%L⇤\n)",
        function,
        parameters.entries.joinToCode(",\n") { (name, value) ->
            CodeBlock.of("⇥%L = %L⇤", name, value)
        }
    )
}

fun createCallSingleVararg(function: CodeBlock, vararg parameters: CodeBlock): CodeBlock {
    return createCallSingleVararg(
        function = function,
        parameters = parameters.asList(),
    )
}

fun createCallSingleVararg(function: CodeBlock, parameters: List<CodeBlock>): CodeBlock {
    if (parameters.isEmpty())
        return CodeBlock.of("%L()", function)

    return CodeBlock.of(
        format = "%L(\n⇥%L⇤\n)",
        function,
        parameters.joinToCode(",\n") {
            CodeBlock.of("⇥%L⇤", it)
        }
    )
}
