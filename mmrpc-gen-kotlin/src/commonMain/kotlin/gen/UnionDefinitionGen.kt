package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.gen.kotlin.UnionStrategy
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.calculateStrategy
import org.cufy.mmrpc.gen.kotlin.common.model.generatedClassName
import org.cufy.mmrpc.gen.kotlin.common.model.nameOfUnionWrapperEntry
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.common.typeSerialName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*

context(ctx: Context, _: FailScope, _: InitStage)
fun doUnionDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is UnionDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            when (element.calculateStrategy()) {
                UnionStrategy.DATA_OBJECT
                -> addDataObject(element)

                UnionStrategy.SEALED_INTERFACE
                -> addSealedInterface(element)

                UnionStrategy.WRAPPER_SEALED_INTERFACE
                -> addWrapperSealedInterface(element)
            }
        }
    }
}

context(_: Context, _: InitStage)
private fun addDataObject(element: UnionDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        data object <name>
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        objectSpec(element.nameOfClass()) {
            addModifiers(KModifier.DATA)

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

context(_: Context, _: InitStage)
private fun addSealedInterface(element: UnionDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        sealed interface <name>
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        interfaceSpec(element.nameOfClass()) {
            addModifiers(KModifier.SEALED)

            addKdoc(createKdocCode(element))
            addAnnotation(createSerializable())
            addAnnotation(createSerialName(element.typeSerialName()))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            applyOf(target = element.canonicalName)
        }
    }

    for (subtype in element.types) {
        inject<TypeSpec.Builder>(target = subtype.canonicalName) {
            addSuperinterface(element.generatedClassName())
        }
    }
}

context(_: Context, _: InitStage)
private fun addWrapperSealedInterface(element: UnionDefinition) {
    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        interfaceSpec(element.nameOfClass()) {
            addModifiers(KModifier.SEALED)

            for (type in element.types) {
                addType(classSpec(type.nameOfUnionWrapperEntry()) {
                    addModifiers(KModifier.VALUE)
                    addAnnotation(JvmInline::class)
                    addSuperinterface(element.generatedClassName())

                    primaryConstructor(constructorSpec {
                        addParameter("value", type.typeName())
                    })
                    addProperty(propertySpec("value", type.typeName()) {
                        initializer("value")
                    })

                    addAnnotation(createSerializable())
                    addAnnotation(createSerialName(type.typeSerialName()))

                    for (usage in type.metadata) {
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
