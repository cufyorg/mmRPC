package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.ConstDefinition
import org.cufy.specdsl.Marker0
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createLiteralOrRefOfValue(element: ConstDefinition): CodeBlock {
    if (element.isAnonymous)
        return createLiteral(element)

    if (element.canonicalName in ctx.nativeElements)
        return createLiteral(element)

    return refOfValue(element)
}
