package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec.Companion.enumBuilder
import org.cufy.mmrpc.EnumDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.anonymousClassSpec
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeEnumDefinition() {
    for (element in ctx.elements) {
        if (element !is EnumDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateEnumClass(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateEnumClass(element: EnumDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        @Serializable()
        @SerialName("<canonical-name>")
        enum class <name>(val value: <type>) {
            [
            <entry-kdoc>
            [ @<entry-metadata> ]
            @SerialName("<entry-canonical-name>")
            <entry-name>(<entry-value>),
            ]
            ;
        }
    }
    */

    createType(element.canonicalName) {
        enumBuilder(element.nameOfClass()).apply {
            primaryConstructor(constructorSpec {
                addParameter("value", element.type.typeName())
            })
            addProperty(propertySpec("value", element.type.typeName()) {
                initializer("%L", "value")
            })

            element.entries.forEach {
                addEnumConstant(it.nameOfEnumEntry(), anonymousClassSpec {
                    addSuperclassConstructorParameter(createLiteralCode(it.type, it.value))

                    addKdoc(createShortKdocCode(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                    addAnnotations(createSerialNameAnnotationSet(CodeBlock.of(it.value.contentToString())))
                })
            }

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
