package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeFaultDefinition() {
    for (element in ctx.elements) {
        if (element !is FaultDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateDataObject(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: FaultDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        data object <name> : FaultObject {
            const val CANONICAL_NAME = "<canonical-name>"

            override val canonicalName = CanonicalName(CANONICAL_NAME)
        }
    }
     */

    createType(element.canonicalName) {
        classBuilder(element.nameOfClass()).apply {
            superclass(Exception::class)

            addType(companionObjectSpec {
                addProperty(propertySpec("CANONICAL_NAME", STRING) {
                    addModifiers(KModifier.CONST)
                    initializer("%S", element.canonicalName.value)
                })
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        }
    }
}
