package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.StructObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.StructStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.calculateStructStrategy
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

class StructDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is StructDefinition) continue
            if (!hasGeneratedClass(element)) continue

            failGenBoundary {
                when (calculateStructStrategy(element)) {
                    StructStrategy.DATA_OBJECT
                    -> applyCreateDataObject(element)

                    StructStrategy.DATA_CLASS
                    -> applyCreateDataClass(element)
                }
            }
        }
    }

    //

    private fun applyCreateDataObject(element: StructDefinition) {
        val superinterface = StructObject::class.asClassName()

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

    private fun applyCreateDataClass(element: StructDefinition) {
        val superinterface = StructObject::class.asClassName()
        val companionObject = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        val primaryConstructor = constructorSpec {
            val parameters = element.structFields.map {
                parameterSpec(asPropertyName(it), typeOf(it.fieldType)) {
                    val default = it.fieldDefault

                    if (default != null) {
                        defaultValue(createLiteral(it.fieldType, default))
                    }
                }
            }

            addParameters(parameters)
        }

        val properties = element.structFields.map {
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
