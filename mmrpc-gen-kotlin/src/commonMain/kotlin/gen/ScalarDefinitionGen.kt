package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeScalarDefinition() {
    for (element in ctx.elements) {
        if (element !is ScalarDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateValueClass(element)
        }
    }
}

context(ctx: GenContext)
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
        classBuilder(element.nameOfClass()).apply {
            addModifiers(KModifier.VALUE)
            addAnnotation(JvmInline::class)

            primaryConstructor(constructorSpec {
                addParameter("value", element.primitiveTypeName())
            })
            addProperty(propertySpec("value", element.primitiveTypeName()) {
                initializer("value")
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
