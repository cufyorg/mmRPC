package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import org.cufy.mmrpc.MapDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.typealiasSpec

context(ctx: GenContext)
fun consumeMapDefinition() {
    for (element in ctx.elements) {
        if (element !is MapDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateTypealias(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateTypealias(element: MapDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        typealias <name> = Map<String, <class-of-type> >
    }
    */

    val target = MAP.parameterizedBy(STRING, element.type.typeName())

    injectFile(element.namespace) {
        addTypeAlias(typealiasSpec(element.nameOfClass(), target) {
            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        })
    }
}
