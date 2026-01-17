package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.gen.kotlin.InterStrategy
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.code.createLiteralCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.*
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.common.typeSerialName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*

context(ctx: Context, _: FailScope, _: InitStage)
fun doInterDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is InterDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            when (element.calculateStrategy()) {
                InterStrategy.DATA_OBJECT
                -> addDataObject(element)

                InterStrategy.DATA_CLASS
                -> addDataClass(element)
            }
        }
    }
}

context(_: Context, _: InitStage)
private fun addDataObject(element: InterDefinition) {
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
private fun addDataClass(element: InterDefinition) {
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
                val <property-name>: <property-type> = <property-default-value>,
            ]
        )
    }
     */

    val fields = element.types.flatMap { it.collectAllSupFields() }.distinctBy { it.name }

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        classSpec(element.nameOfClass()) {
            addModifiers(KModifier.DATA)

            primaryConstructor(constructorSpec {
                for (field in fields) {
                    addParameter(parameterSpec(field.nameOfProperty(), field.type.typeName()) {
                        val default = field.default

                        if (default != null) {
                            defaultValue(createLiteralCode(field.type, default))
                        }
                    })
                }
            })

            for (field in fields) {
                addProperty(propertySpec(field.nameOfProperty(), field.type.typeName()) {
                    initializer(field.nameOfProperty())

                    addKdoc(createKdocCode(field))
                    addAnnotation(createSerialName(field.propertySerialName()))

                    for (usage in field.metadata) {
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
