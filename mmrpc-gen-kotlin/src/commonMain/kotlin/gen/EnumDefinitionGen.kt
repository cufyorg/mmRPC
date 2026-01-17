package org.cufy.mmrpc.gen.kotlin.gen

import org.cufy.mmrpc.EnumDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.code.createLiteralCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.enumEntrySerialName
import org.cufy.mmrpc.gen.kotlin.common.model.nameOfEnumEntry
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.common.typeName
import org.cufy.mmrpc.gen.kotlin.common.typeSerialName
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.*

context(ctx: Context, _: FailScope, _: InitStage)
fun doEnumDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is EnumDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            addEnumClass(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addEnumClass(element: EnumDefinition) {
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

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        enumClassSpec(element.nameOfClass()) {
            primaryConstructor(constructorSpec {
                addParameter("value", element.type.typeName())
            })
            addProperty(propertySpec("value", element.type.typeName()) {
                initializer("%L", "value")
            })

            for (entry in element.entries) {
                addEnumConstant(entry.nameOfEnumEntry(), anonymousClassSpec {
                    addSuperclassConstructorParameter(createLiteralCode(entry.type, entry.value))

                    addKdoc(createKdocCode(entry))
                    addAnnotation(createSerialName(entry.enumEntrySerialName()))

                    for (usage in entry.metadata) {
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

            addType(companionObjectSpec {
                applyOf(target = element.canonicalName)
            })
        }
    }
}
