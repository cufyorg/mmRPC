package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.TupleStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import org.cufy.mmrpc.gen.kotlin.util.xth

context(ctx: GenContext)
fun consumeTupleDefinition() {
    for (element in ctx.elements) {
        if (element !is TupleDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            when (element.calculateStrategy()) {
                TupleStrategy.DATA_OBJECT
                -> applyCreateDataObject(element)

                TupleStrategy.DATA_CLASS
                -> applyCreateDataClass(element)
            }
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: TupleDefinition) {
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
            addAnnotations(createSerialNameAnnotationSet(element.typeSerialName()))
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataClass(element: TupleDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        data class <name>(
            [
                <property-kdoc>
                [ @<property-metadata> ]
                @SerialName("<property-name>")
                val <property-position>: <property-type>,
            ]
        )
    }
     */

    createType(element.canonicalName) {
        classBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.DATA)

            primaryConstructor(constructorSpec {
                addParameters(element.types.mapIndexed { position, type ->
                    parameterSpec(xth(position), type.typeName())
                })
            })
            addProperties(element.types.mapIndexed { position, type ->
                propertySpec(xth(position), type.typeName()) {
                    initializer(xth(position))

                    addKdoc(createShortKdocCode(type))
                    addAnnotations(createAnnotationSet(type.metadata))
                    addAnnotations(createSerialNameAnnotationSet(position.toString()))
                }
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.typeSerialName()))
        }
    }
}
