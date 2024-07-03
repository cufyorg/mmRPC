package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.signatureOf

@Marker0
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
