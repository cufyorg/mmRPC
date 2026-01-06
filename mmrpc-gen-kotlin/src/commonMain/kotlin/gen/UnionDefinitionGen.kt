package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.interfaceBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.UnionStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.classSpec
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeUnionDefinition() {
    for (element in ctx.elements) {
        if (element !is UnionDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            when (element.calculateStrategy()) {
                UnionStrategy.DATA_OBJECT
                -> applyCreateDataObject(element)

                UnionStrategy.SEALED_INTERFACE
                -> applyCreateSealedInterface(element)

                UnionStrategy.WRAPPER_SEALED_INTERFACE
                -> applyCreateWrapperSealedInterface(element)
            }
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: UnionDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        data object <name>
    }
     */

    createType(element.canonicalName) {
        objectBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.DATA)

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}

context(ctx: GenContext)
private fun applyCreateSealedInterface(element: UnionDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        sealed interface <name>
    }
     */

    createType(element.canonicalName) {
        interfaceBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.SEALED)

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }

    for (subtype in element.types) {
        injectType(subtype.canonicalName) {
            addSuperinterface(element.canonicalName.generatedClassName())
        }
    }
}

context(ctx: GenContext)
private fun applyCreateWrapperSealedInterface(element: UnionDefinition) {
    createType(element.canonicalName) {
        interfaceBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.SEALED)

            addTypes(element.types.map {
                classSpec(it.nameOfUnionWrapperEntry()) {
                    addModifiers(KModifier.VALUE)
                    addAnnotation(JvmInline::class)
                    addSuperinterface(element.canonicalName.generatedClassName())

                    primaryConstructor(constructorSpec {
                        addParameter("value", it.typeName())
                    })
                    addProperty(propertySpec("value", it.typeName()) {
                        initializer("value")
                    })

                    addKdoc(createShortKdocCode(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                    addAnnotations(createSerializableAnnotationSet())
                    addAnnotations(createSerialNameAnnotationSet(it.canonicalName.value))
                }
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
