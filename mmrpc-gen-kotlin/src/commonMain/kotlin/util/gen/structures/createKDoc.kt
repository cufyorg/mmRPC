package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.signatureOf

@Marker3
fun GenScope.createKDoc(element: ElementDefinition): CodeBlock {
    return CodeBlock.Builder().apply {
        add(buildString {
            append("### ")
            append(signatureOf(element))

            if (element.description.isNotBlank()) {
                appendLine()
                appendLine()
                append(element.description)
                appendLine()
            }
        })
    }.build()
}
