package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.TypeObject
import org.cufy.mmrpc.UnionDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.UnionStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.calculateUnionStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.common.*
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.asUnionEntryName
import org.cufy.mmrpc.gen.kotlin.util.gen.references.generatedClassOf
import org.cufy.mmrpc.gen.kotlin.util.gen.references.typeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDocShort
import org.cufy.mmrpc.gen.kotlin.util.poet.classSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.companionObjectSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class UnionDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is UnionDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failGenBoundary {
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
        val superinterface = TypeObject::class.asClassName()

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

    private fun applyCreateSealedInterface(element: UnionDefinition) {
        val superinterface = TypeObject::class.asClassName()
        val companionObjectSpec = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        createInterface(element) {
            addModifiers(KModifier.SEALED)
            addSuperinterface(superinterface)
            addType(companionObjectSpec)

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }

        for (unionType in element.unionTypes) {
            on(unionType) {
                addSuperinterface(generatedClassOf(element))
            }
        }
    }

    private fun applyCreateWrapperSealedInterface(element: UnionDefinition) {
        val superinterface = TypeObject::class.asClassName()
        val companionObject = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        val types = element.unionTypes.map {
            val itCompanionObject = companionObjectSpec {
                addProperty(createDelegateStaticInfoProperty(it))
            }

            val itPrimaryConstructor = constructorSpec {
                addParameter("value", typeOf(it))
            }
            val itValue = propertySpec("value", typeOf(it)) {
                initializer("value")
            }

            classSpec(asUnionEntryName(it)) {
                addModifiers(KModifier.VALUE)
                addAnnotation(JvmInline::class)
                addSuperinterface(generatedClassOf(element))
                addType(itCompanionObject)
                primaryConstructor(itPrimaryConstructor)
                addProperty(itValue)
                addProperty(createOverrideObjectInfoProperty(it))

                addKdoc(createKDocShort(it))
                addAnnotations(createAnnotationSet(it.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(it.canonicalName.value))
            }
        }

        createInterface(element) {
            addModifiers(KModifier.SEALED)
            addSuperinterface(superinterface)
            addType(companionObject)
            addTypes(types)

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
