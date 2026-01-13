package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.StructStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeStructDefinition() {
    for (element in ctx.elements) {
        if (element !is StructDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            when (element.calculateStrategy()) {
                StructStrategy.DATA_OBJECT
                -> applyCreateDataObject(element)

                StructStrategy.DATA_CLASS
                -> applyCreateDataClass(element)
            }
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: StructDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        data object <name> : <traits>
    }
     */

    createType(element.canonicalName) {
        objectBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.DATA)
            addSuperinterfaces(element.traits.map { it.canonicalName.generatedClassName() })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.typeSerialName()))
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataClass(element: StructDefinition) {
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

    createType(element.canonicalName) {
        classBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.DATA)
            addSuperinterfaces(element.traits.map { it.canonicalName.generatedClassName() })

            primaryConstructor(constructorSpec {
                addParameters(element.fieldsInherited().map {
                    parameterSpec(it.nameOfProperty(), it.type.typeName()) {
                        val default = it.default

                        if (default != null) {
                            defaultValue(createLiteralCode(it.type, default))
                        }
                    }
                })
                addParameters(element.fields.map {
                    parameterSpec(it.nameOfProperty(), it.type.typeName()) {
                        val default = it.default

                        if (default != null) {
                            defaultValue(createLiteralCode(it.type, default))
                        }
                    }
                })
            })
            addProperties(element.fieldsInherited().map {
                propertySpec(it.nameOfProperty(), it.type.typeName()) {
                    addModifiers(KModifier.OVERRIDE)

                    initializer(it.nameOfProperty())

                    addKdoc(createShortKdocCode(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                    addAnnotations(createSerialNameAnnotationSet(it.propertySerialName()))
                }
            })
            addProperties(element.fields.map {
                propertySpec(it.nameOfProperty(), it.type.typeName()) {
                    initializer(it.nameOfProperty())

                    addKdoc(createShortKdocCode(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                    addAnnotations(createSerialNameAnnotationSet(it.propertySerialName()))
                }
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.typeSerialName()))
        }
    }
}
