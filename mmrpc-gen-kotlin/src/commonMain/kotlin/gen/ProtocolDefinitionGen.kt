package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
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
            applyCreateDataObject(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: ProtocolDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        data object <name>
    }
    */
    createType(element.canonicalName) {
        objectBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.DATA)

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        }
    }
}
