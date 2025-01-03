package org.cufy.mmrpc.gen.kotlin.core

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.TupleObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.gen.TupleStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.calculateTupleStrategy
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createOverrideObjectInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerialNameAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerializableAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.references.typeOf
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDocShort
import org.cufy.mmrpc.gen.kotlin.util.poet.*
import org.cufy.mmrpc.gen.kotlin.util.xth

class TupleDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is TupleDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failGenBoundary {
                when (calculateTupleStrategy(element)) {
                    TupleStrategy.DATA_OBJECT
                    -> applyCreateDataObject(element)

                    TupleStrategy.DATA_CLASS
                    -> applyCreateDataClass(element)
                }
            }
        }
    }

    //

    private fun applyCreateDataObject(element: TupleDefinition) {
        val superinterface = TupleObject::class.asClassName()

        createObject(element) {
            addModifiers(KModifier.DATA)
            addSuperinterface(superinterface)
            addProperty(createStaticInfoProperty(element))
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))

            // toList() implementation
            addFunction(funSpec("toList") {
                addModifiers(KModifier.OVERRIDE)
                returns(LIST.parameterizedBy(ANY.copy(nullable = true)))
                addStatement("return emptyList()")
            })
        }
    }

    private fun applyCreateDataClass(element: TupleDefinition) {
        val superinterface = TupleObject::class.asClassName()
        val companionObject = companionObjectSpec {
            addProperty(createStaticInfoProperty(element))
        }

        val primaryConstructor = constructorSpec {
            val parameters = element.tupleTypes.mapIndexed { position, type ->
                parameterSpec(xth(position), typeOf(type))
            }

            addParameters(parameters)
        }

        val properties = element.tupleTypes.mapIndexed { position, type ->
            propertySpec(xth(position), typeOf(type)) {
                initializer(xth(position))

                addKdoc(createKDocShort(type))
                addAnnotations(createAnnotationSet(type.metadata))
                addAnnotations(createSerialNameAnnotationSet(type.name))
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

            // toList() implementation
            addFunction(funSpec("toList") {
                addModifiers(KModifier.OVERRIDE)
                returns(LIST.parameterizedBy(ANY.copy(nullable = true)))
                addStatement("return %L", createCallSingleVararg(
                    function = CodeBlock.of("listOf"),
                    List(element.tupleTypes.size) { position ->
                        CodeBlock.of("%L", xth(position))
                    }
                ))
            })
        }
    }
}
