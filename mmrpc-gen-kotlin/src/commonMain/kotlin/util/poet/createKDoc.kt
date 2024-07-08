package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.signatureOf

@Marker3
fun GenGroup.createKDoc(element: ElementDefinition): CodeBlock {
    return CodeBlock.Builder().apply {
        add(buildString {
            append("### ")
            append(signatureOf(element))
            appendLine()
            appendLine()
            if (element.description.isNotBlank()) {
                append(element.description)
                appendLine()
            }
        })
    }.build()
}
