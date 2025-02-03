package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.asClassName
import org.cufy.mmrpc.gen.kotlin.common.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.common.createKDoc
import org.cufy.mmrpc.gen.kotlin.common.hasGeneratedClass

class ProtocolDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is ProtocolDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateDataObject(element)
            }
        }
    }

    private fun applyCreateDataObject(element: ProtocolDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            data object <name>
        }
        */
        createType(element.canonicalName) {
            objectBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
            }
        }
    }
}
