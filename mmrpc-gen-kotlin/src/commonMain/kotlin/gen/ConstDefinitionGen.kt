package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.ConstDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenFeature
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class ConstDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
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

    private fun applyCreateProperty(element: ConstDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            const val <name> = <value>
        }
        */

        injectScope(element.namespace) {
            addProperty(propertySpec(asPropertyName(element), classOf(element.type)) {
                if (isCompileConst(element.type))
                    addModifiers(KModifier.CONST)

                initializer(createLiteral(element))

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
            })
        }
    }
}
