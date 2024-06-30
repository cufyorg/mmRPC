package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.ConstDefinition
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.ScalarDefinition
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createLiteralInlinedOrReference(element: ConstDefinition): CodeBlock {
    if (element.isAnonymous)
        return createLiteralInlined(element)

    if (element.canonicalName in ctx.nativeElements)
        return createLiteralInlined(element)

    if (element.constType !is ScalarDefinition)
        return createLiteralInlined(element)

    if (element.constType.canonicalName !in ctx.nativeElements)
        return createLiteralInlined(element)

    return referenceOf(element)
}
