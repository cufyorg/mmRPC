package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.interfaceBuilder
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.TraitStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeTraitDefinition() {
    for (element in ctx.elements) {
        if (element !is TraitDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateInterface(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateInterface(element: TraitDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        <maybe-sealed-modifier> interface <name> : <traits> {
            [
                <property-kdoc>
                [ @<property-metadata> ]
                @SerialName("<property-name>")
                val <property-name>: <property-type>
            ]
        }
    }
     */

    createType(element.canonicalName) {
        interfaceBuilder(element.nameOfClass()).apply {
            addSuperinterfaces(element.traits.map { it.canonicalName.generatedClassName() })

            if (element.calculateStrategy() == TraitStrategy.SEALED_INTERFACE)
                addModifiers(KModifier.SEALED)

            addProperties(element.fields.map {
                propertySpec(it.nameOfProperty(), it.type.typeName()) {
                    addKdoc(createShortKdocCode(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                    addAnnotations(createSerialNameAnnotationSet(it.propertySerialName()))
                }
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.typeSerialName()))
        }
    }
}
