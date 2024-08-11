package org.cufy.mmrpc.gen.kotlin.core

import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.asPropertyName
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.metadataTypeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDocShort
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createMetadataLiteral
import org.cufy.mmrpc.gen.kotlin.util.poet.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class MetadataDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is MetadataDefinition) continue
            if (!hasGeneratedClass(element)) continue

            failGenBoundary {
                applyCreateAnnotationClass(element)
            }
        }
    }

    private fun applyCreateAnnotationClass(element: MetadataDefinition) {
        val companionObject = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        val primaryConstructor = constructorSpec {
            val parameters = element.metadataFields.map {
                parameterSpec(it.asPropertyName, metadataTypeOf(it.fieldType)) {
                    val default = it.fieldDefault

                    if (default != null) {
                        defaultValue(createMetadataLiteral(it.fieldType, default))
                    }
                }
            }

            addParameters(parameters)
        }

        val properties = element.metadataFields.map {
            propertySpec(it.asPropertyName, metadataTypeOf(it.fieldType)) {
                initializer(it.asPropertyName)

                addKdoc(createKDocShort(it))
                addAnnotations(createAnnotationSet(it.metadata))
            }
        }

        createAnnotation(element) {
            addType(companionObject)
            primaryConstructor(primaryConstructor)
            addProperties(properties)

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
        }
    }
}
