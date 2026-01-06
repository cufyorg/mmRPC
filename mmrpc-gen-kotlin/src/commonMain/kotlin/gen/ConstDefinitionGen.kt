package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeConstDefinition() {
    if (GenFeature.GEN_CONST_VALUE_PROPERTIES !in ctx.features)
        return

    for (element in ctx.elements) {
        if (element !is ConstDefinition) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateProperty(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateProperty(element: ConstDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        const val <name> = <value>
    }
    */

    injectScope(element.namespace) {
        addProperty(propertySpec(element.nameOfProperty(), element.type.typeName()) {
            if (element.type.isCompileConst())
                addModifiers(KModifier.CONST)

            initializer(createLiteralCode(element.type, element.value))

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        })
    }
}
