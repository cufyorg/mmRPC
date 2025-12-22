package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.InterStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeInterDefinition() {
    for (element in ctx.elements) {
        if (element !is InterDefinition) continue
        if (!hasGeneratedClass(element)) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            when (calculateInterStrategy(element)) {
                InterStrategy.DATA_OBJECT
                -> applyCreateDataObject(element)

                InterStrategy.DATA_CLASS
                -> applyCreateDataClass(element)
            }
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataObject(element: InterDefinition) {
    createType(element.canonicalName) {
        objectBuilder(asClassName(element)).apply {
            addModifiers(KModifier.DATA)

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}

context(ctx: GenContext)
private fun applyCreateDataClass(element: InterDefinition) {
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

    val fields = element.types.flatMap { it.fields }.distinctBy { it.name }

    createType(element.canonicalName) {
        classBuilder(asClassName(element)).apply {
            addModifiers(KModifier.DATA)

            primaryConstructor(constructorSpec {
                addParameters(fields.map {
                    parameterSpec(asPropertyName(it), classOf(it.type)) {
                        val default = it.default

                        if (default != null) {
                            defaultValue(createLiteral(it.type, default))
                        }
                    }
                })
            })
            addProperties(fields.map {
                propertySpec(asPropertyName(it), classOf(it.type)) {
                    initializer(asPropertyName(it))

                    addKdoc(createKDocShort(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                    addAnnotations(createSerialNameAnnotationSet(it.name))
                }
            })

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
