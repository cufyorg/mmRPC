package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class ScalarDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is ScalarDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateValueClass(element)
            }
        }
    }

    private fun applyCreateValueClass(element: ScalarDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            @JvmInline
            @Serializable()
            @SerialName("<canonical-name>")
            data class <name>(val value: <type>)
        }
         */

        createType(element.canonicalName) {
            classBuilder(asClassName(element)).apply {
                addModifiers(KModifier.VALUE)
                addAnnotation(JvmInline::class)

                primaryConstructor(constructorSpec {
                    addParameter("value", primitiveClassOf(element))
                })
                addProperty(propertySpec("value", primitiveClassOf(element)) {
                    initializer("value")
                })

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
            }
        }
    }
}
