package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.TypeSpec.Companion.interfaceBuilder
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*

context(ctx: GenContext)
fun consumeProtocolDefinition() {
    for (element in ctx.elements) {
        if (element !is ProtocolDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateInterface(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateInterface(element: ProtocolDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        interface <name> {
        }
    }
    */
    createType(element.canonicalName) {
        interfaceBuilder(element.nameOfClass()).apply {
            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        }
    }
}
