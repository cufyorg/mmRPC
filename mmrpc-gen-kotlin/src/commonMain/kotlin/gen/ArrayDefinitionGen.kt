package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.ArrayDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.typealiasSpec

context(ctx: GenContext)
fun consumeArrayDefinition() {
    for (element in ctx.elements) {
        if (element !is ArrayDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateTypealias(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateTypealias(element: ArrayDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        typealias <name> = List< <class-of-type> >
    }
    */

    injectFile(element.namespace) {
        addTypeAlias(typealiasSpec(element.nameOfClass(), LIST.parameterizedBy(element.type.typeName())) {
            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        })
    }
}
