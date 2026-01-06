package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext

@Marker3
context(ctx: GenContext)
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

@Marker3
context(ctx: GenContext)
fun createShortKdocCode(element: ElementDefinition): CodeBlock {
    if (element.hasGeneratedClass())
        return CodeBlock.of("@see [%L]", element.canonicalName.generatedClassName())

    if (element is ScalarDefinition)
        if (element.isNative() || element.isUserdefined())
            return CodeBlock.of("### %L", element.humanSignature())

    if (element is MetadataDefinition)
        if (element.isNative() || element.isUserdefined())
            return CodeBlock.of("### %L", element.humanSignature())

    return createKdocCode(element)
}
