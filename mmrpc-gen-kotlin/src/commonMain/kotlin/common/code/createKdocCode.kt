package org.cufy.mmrpc.gen.kotlin.common.code

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.gen.kotlin.ContextScope
import org.cufy.mmrpc.gen.kotlin.common.humanSignature
import org.cufy.mmrpc.gen.kotlin.context.Context

@ContextScope
context(_: Context)
fun createKdocCode(element: ElementDefinition): CodeBlock {
    return CodeBlock.Builder().apply {
        add(buildString {
            append("### ")
            append(element.humanSignature())

            if (element.description.isNotBlank()) {
                appendLine()
                appendLine()
                append(element.description)
                appendLine()
            }
        })
    }.build()
}
