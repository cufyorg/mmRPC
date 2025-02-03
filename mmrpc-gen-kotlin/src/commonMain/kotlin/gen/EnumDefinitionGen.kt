package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec.Companion.enumBuilder
import org.cufy.mmrpc.EnumDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.anonymousClassSpec
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class EnumDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is EnumDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateEnumClass(element)
            }
        }
    }

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
            enumBuilder(asClassName(element)).apply {
                primaryConstructor(constructorSpec {
                    addParameter("value", classOf(element.type))
                })
                addProperty(propertySpec("value", classOf(element.type)) {
                    initializer("%L", "value")
                })

                element.entries.forEach {
                    addEnumConstant(asEnumEntryName(it), anonymousClassSpec {
                        addSuperclassConstructorParameter(createLiteral(it))

                        addKdoc(createKDocShort(it))
                        addAnnotations(createAnnotationSet(it.metadata))
                        addAnnotations(createSerialNameAnnotationSet(CodeBlock.of(it.value.contentToString())))
                    })
                }

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
                addAnnotations(createSerializableAnnotationSet())
                addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
            }
        }
    }
}
