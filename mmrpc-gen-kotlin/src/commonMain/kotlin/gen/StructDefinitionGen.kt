package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.StructStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class StructDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is StructDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
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
            objectBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
            }
        }
    }

    private fun applyCreateDataClass(element: StructDefinition) {
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

        createType(element.canonicalName) {
            classBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)

                primaryConstructor(constructorSpec {
                    addParameters(element.fields.map {
                        parameterSpec(asPropertyName(it), classOf(it.type)) {
                            val default = it.default

                            if (default != null) {
                                defaultValue(createLiteral(it.type, default))
                            }
                        }
                    })
                })
                addProperties(element.fields.map {
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
}
