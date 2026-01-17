package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.gen.kotlin.TraitStrategy
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.common.typeSerialName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.createSerialName
import org.cufy.mmrpc.gen.kotlin.util.createSerializable
import org.cufy.mmrpc.gen.kotlin.util.interfaceSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: Context, _: FailScope, _: InitStage)
fun doTraitDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is TraitDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            when (element.calculateStrategy()) {
                TraitStrategy.INTERFACE
                -> addInterface(element)

                TraitStrategy.SEALED_INTERFACE
                -> addSealedInterface(element)
            }
        }
    }
}

context(_: Context, _: InitStage)
private fun addInterface(element: TraitDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        interface <name> : <traits> {
            [
                <property-kdoc>
                [ @<property-metadata> ]
                @SerialName("<property-name>")
                val <property-name>: <property-type>
            ]
        }
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        interfaceSpec(element.nameOfClass()) {
            for (trait in element.traits) {
                addSuperinterface(trait.generatedClassName())
            }

            for (field in element.fields) {
                addProperty(propertySpec(field.nameOfProperty(), field.type.typeName()) {
                    addKdoc(createKdocCode(field))
                    addAnnotation(createSerialName(field.propertySerialName()))

                    for (usage in field.metadata) {
                        addAnnotation(usage.annotationSpec())
                    }
                })
            }

            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            applyOf(target = element.canonicalName)
        }
    }
}

context(_: Context, _: InitStage)
private fun addSealedInterface(element: TraitDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        sealed interface <name> : <traits> {
            [
                <property-kdoc>
                [ @<property-metadata> ]
                @SerialName("<property-name>")
                val <property-name>: <property-type>
            ]
        }
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        interfaceSpec(element.nameOfClass()) {
            addModifiers(KModifier.SEALED)

            for (trait in element.traits) {
                addSuperinterface(trait.generatedClassName())
            }

            for (field in element.fields) {
                addProperty(propertySpec(field.nameOfProperty(), field.type.typeName()) {
                    addKdoc(createKdocCode(field))
                    addAnnotation(createSerialName(field.propertySerialName()))

                    for (usage in field.metadata) {
                        addAnnotation(usage.annotationSpec())
                    }
                })
            }

            addKdoc(createKdocCode(element))
            addAnnotation(createSerializable())
            addAnnotation(createSerialName(element.typeSerialName()))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            applyOf(target = element.canonicalName)
        }
    }
}
