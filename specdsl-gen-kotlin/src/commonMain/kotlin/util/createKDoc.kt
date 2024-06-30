package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.MetadataParameterDefinition
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createKDoc(element: ElementDefinition): String {
    return buildString {
        append("### ")
        append(signatureOf(element))
        appendLine()
        appendLine()
        append(element.description)

        if (element is MetadataParameterDefinition) {
            element.parameterDefault?.let {
                if (!it.isAnonymous && it.canonicalName !in ctx.nativeElements) {
                    appendLine()
                    appendLine()
                    append("@see ${createKDocReference(it)}")
                }
            }
        }
    }
}
