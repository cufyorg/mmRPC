package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker3
fun GenGroup.createLiteralOrRefOfValue(element: ConstDefinition): CodeBlock {
    if (element.isAnonymous)
        return createLiteral(element)

    if (element.canonicalName in ctx.nativeElements)
        return createLiteral(element)

    return refOfValue(element)
}
