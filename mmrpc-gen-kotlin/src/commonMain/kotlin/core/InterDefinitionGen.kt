package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.InterObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.gen.InterStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.calculateInterStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createOverrideObjectInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerialNameAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerializableAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.asPropertyName
import org.cufy.mmrpc.gen.kotlin.util.gen.references.typeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDocShort
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createLiteral
import org.cufy.mmrpc.gen.kotlin.util.poet.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class InterDefinitionGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is InterDefinition) continue
            if (!hasGeneratedClass(element)) continue

            failGenBoundary {
                when (calculateInterStrategy(element)) {
                    InterStrategy.DATA_OBJECT
                    -> applyCreateDataObject(element)

                    InterStrategy.DATA_CLASS
                    -> applyCreateDataClass(element)
                }
            }
        }
    }

    //

    private fun applyCreateDataObject(element: InterDefinition) {
        val superinterface = InterObject::class.asClassName()

        createObject(element) {
            addModifiers(KModifier.DATA)
            addSuperinterface(superinterface)
            addProperty(createStaticInfoProperty(element))
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }

    private fun applyCreateDataClass(element: InterDefinition) {
        val combinedFields = element.interTypes.asSequence()
            .flatMap { it.structFields }
            .distinctBy { it.name }
            .toList()

        val superinterface = InterObject::class.asClassName()
        val companionObject = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        val primaryConstructor = constructorSpec {
            val parameters = combinedFields.map {
                parameterSpec(asPropertyName(it), typeOf(it.fieldType)) {
                    val default = it.fieldDefault

                    if (default != null) {
                        defaultValue(createLiteral(it.fieldType, default))
                    }
                }
            }

            addParameters(parameters)
        }

        val properties = combinedFields.map {
            propertySpec(asPropertyName(it), typeOf(it.fieldType)) {
                initializer(asPropertyName(it))

                addKdoc(createKDocShort(it))
                addAnnotations(createAnnotationSet(it.metadata))
                addAnnotations(createSerialNameAnnotationSet(it.name))
            }
        }

        createClass(element) {
            addModifiers(KModifier.DATA)
            addSuperinterface(superinterface)
            addType(companionObject)
            primaryConstructor(primaryConstructor)
            addProperties(properties)
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
