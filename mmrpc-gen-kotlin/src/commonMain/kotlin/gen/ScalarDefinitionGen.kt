package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.KModifier
import org.cufy.mmrpc.ScalarDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.primitiveTypeName
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeSerialName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*

context(ctx: Context, _: FailScope, _: InitStage)
fun doScalarDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is ScalarDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            addValueClass(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addValueClass(element: ScalarDefinition) {
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

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        classSpec(element.nameOfClass()) {
            addModifiers(KModifier.VALUE)
            addAnnotation(JvmInline::class)

            primaryConstructor(constructorSpec {
                addParameter("value", element.primitiveTypeName())
            })
            addProperty(propertySpec("value", element.primitiveTypeName()) {
                initializer("value")
            })

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
