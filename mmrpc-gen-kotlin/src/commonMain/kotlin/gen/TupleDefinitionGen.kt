package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.gen.kotlin.TupleStrategy
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.calculateStrategy
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.common.typeSerialName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*

context(ctx: Context, _: FailScope, _: InitStage)
fun doTupleDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is TupleDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            when (element.calculateStrategy()) {
                TupleStrategy.DATA_OBJECT
                -> addDataObject(element)

                TupleStrategy.DATA_CLASS
                -> addDataClass(element)
            }
        }
    }
}

context(_: Context, _: InitStage)
private fun addDataObject(element: TupleDefinition) {
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
private fun addDataClass(element: TupleDefinition) {
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

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        classSpec(element.nameOfClass()) {
            addModifiers(KModifier.DATA)

            primaryConstructor(constructorSpec {
                for ((i, type) in element.types.withIndex()) {
                    addParameter(parameterSpec(xth(i), type.typeName()))
                }
            })

            for ((i, type) in element.types.withIndex()) {
                addProperty(propertySpec(xth(i), type.typeName()) {
                    initializer(xth(i))

                    addAnnotation(createSerialName(i.toString()))

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
