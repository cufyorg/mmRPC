package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeFieldDefinition() {
    if (GenFeature.GEN_FIELD_NAME_PROPERTIES !in ctx.features)
        return

    for (element in ctx.elements) {
        if (element !is FieldDefinition) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateNameProperty(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateNameProperty(element: FieldDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        const val <name> = "<name>"
    }
    */

    injectScope(element.namespace) {
        addProperty(propertySpec(asNamePropertyName(element), STRING) {
            addModifiers(KModifier.CONST)
            initializer("%S", element.name)

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
        })
    }
}
