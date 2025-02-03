package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.interfaceBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.UnionStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.classSpec
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class UnionDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is UnionDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                when (calculateUnionStrategy(element)) {
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

    //

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
            objectBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
            }
        }
    }

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
            interfaceBuilder(asClassName(element)).apply {
                addModifiers(KModifier.SEALED)

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
            }
        }

        for (subtype in element.types) {
            injectType(subtype.canonicalName) {
                addSuperinterface(generatedClassOf(element.canonicalName))
            }
        }
    }

    private fun applyCreateWrapperSealedInterface(element: UnionDefinition) {
        createType(element.canonicalName) {
            interfaceBuilder(asClassName(element)).apply {
                addModifiers(KModifier.SEALED)

                addTypes(element.types.map {
                    classSpec(asUnionWrapperEntryName(it)) {
                        addModifiers(KModifier.VALUE)
                        addAnnotation(JvmInline::class)
                        addSuperinterface(generatedClassOf(element.canonicalName))

                        primaryConstructor(constructorSpec {
                            addParameter("value", classOf(it))
                        })
                        addProperty(propertySpec("value", classOf(it)) {
                            initializer("value")
                        })

                        addKdoc(createKDocShort(it))
                        addAnnotations(createAnnotationSet(it.metadata))
                        addAnnotations(createSerializableAnnotationSet())
                        addAnnotations(createSerialNameAnnotationSet(it.canonicalName.value))
                    }
                })

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
            }
        }
    }
}
