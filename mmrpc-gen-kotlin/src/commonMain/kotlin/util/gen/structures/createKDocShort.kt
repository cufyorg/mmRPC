package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.isNative
import org.cufy.mmrpc.gen.kotlin.util.gen.isUserdefined
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.signatureOf

@Marker3
fun GenScope.createKDocShort(element: ElementDefinition): CodeBlock {
    if (hasGeneratedClass(element))
        return CodeBlock.of("@see [%L]", generatedClassOf(element))

    if (element is ScalarDefinition)
        if (isNative(element) || isUserdefined(element))
            return CodeBlock.of("### %L", signatureOf(element))

    if (element is MetadataDefinition)
        if (isNative(element) || isUserdefined(element))
            return CodeBlock.of("### %L", signatureOf(element))

    return createKDoc(element)
}
