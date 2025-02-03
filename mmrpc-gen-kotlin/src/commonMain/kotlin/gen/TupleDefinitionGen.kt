package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.TypeSpec.Companion.objectBuilder
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.TupleStrategy
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec
import org.cufy.mmrpc.gen.kotlin.util.xth

class TupleDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is TupleDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
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
                    val <property-name>: <property-type>,
                ]
            )
        }
         */

        createType(element.canonicalName) {
            classBuilder(asClassName(element)).apply {
                addModifiers(KModifier.DATA)

                primaryConstructor(constructorSpec {
                    addParameters(element.types.mapIndexed { position, type ->
                        parameterSpec(xth(position), classOf(type))
                    })
                })
                addProperties(element.types.mapIndexed { position, type ->
                    propertySpec(xth(position), classOf(type)) {
                        initializer(xth(position))

                        addKdoc(createKDocShort(type))
                        addAnnotations(createAnnotationSet(type.metadata))
                        addAnnotations(createSerialNameAnnotationSet(type.name))
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
