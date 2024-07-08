package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.Marker0
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createLiteralInlinedOrRefOfValue(element: ConstDefinition): CodeBlock {
    if (element.isAnonymous)
        return createLiteralInlined(element)

    if (element.canonicalName in ctx.nativeElements)
        return createLiteralInlined(element)

    if (element.constType !is ScalarDefinition)
        return createLiteralInlined(element)

    if (element.constType.canonicalName !in ctx.nativeElements)
        return createLiteralInlined(element)

    return refOfValue(element)
}
