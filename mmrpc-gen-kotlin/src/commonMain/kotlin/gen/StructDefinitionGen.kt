package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.gen.kotlin.StructStrategy
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
fun doStructDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is StructDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            when (element.calculateStrategy()) {
                StructStrategy.DATA_OBJECT
                -> addDataObject(element)

                StructStrategy.DATA_CLASS
                -> addDataClass(element)
            }
        }
    }
}

context(_: Context, _: InitStage)
private fun addDataObject(element: StructDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        data object <name> : <traits>
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        objectSpec(element.nameOfClass()) {
            addModifiers(KModifier.DATA)

            for (trait in element.traits) {
                addSuperinterface(trait.generatedClassName())
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

context(_: Context, _: InitStage)
private fun addDataClass(element: StructDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        data class <name>(
            [
                <trait-property-kdoc>
                [ @<trait-property-metadata> ]
                @SerialName("<trait-property-name>")
                override val <trait-property-name>: <trait-property-type> = <trait-property-default-value>,
            ],
            [
                <property-kdoc>
                [ @<property-metadata> ]
                @SerialName("<property-name>")
                val <property-name>: <property-type> = <property-default-value>,
            ]
        ) : <traits>
    }
     */

    val supFields = element.collectAllSupFields().toList()

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        classSpec(element.nameOfClass()) {
            addModifiers(KModifier.DATA)

            for (trait in element.traits) {
                addSuperinterface(trait.generatedClassName())
            }

            primaryConstructor(constructorSpec {
                for (field in supFields) {
                    addParameter(parameterSpec(field.nameOfProperty(), field.type.typeName()) {
                        val default = field.default

                        if (default != null) {
                            defaultValue(createLiteralCode(field.type, default))
                        }
                    })
                }

                for (field in element.fields) {
                    addParameter(parameterSpec(field.nameOfProperty(), field.type.typeName()) {
                        val default = field.default

                        if (default != null) {
                            defaultValue(createLiteralCode(field.type, default))
                        }
                    })
                }
            })

            for (field in supFields) {
                addProperty(propertySpec(field.nameOfProperty(), field.type.typeName()) {
                    addModifiers(KModifier.OVERRIDE)

                    initializer(field.nameOfProperty())

                    addKdoc(createKdocCode(field))
                    addAnnotation(createSerialName(field.propertySerialName()))

                    for (usage in field.metadata) {
                        addAnnotation(usage.annotationSpec())
                    }
                })
            }

            for (field in element.fields) {
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
