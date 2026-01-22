package org.cufy.mmrpc.gen.kotlin.gen

import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.common.code.createKdocCode
import org.cufy.mmrpc.gen.kotlin.common.isGeneratingClass
import org.cufy.mmrpc.gen.kotlin.common.metaTypeName
import org.cufy.mmrpc.gen.kotlin.common.model.annotationSpec
import org.cufy.mmrpc.gen.kotlin.common.model.metaParameterSpec
import org.cufy.mmrpc.gen.kotlin.common.model.nameOfProperty
import org.cufy.mmrpc.gen.kotlin.common.nameOfClass
import org.cufy.mmrpc.gen.kotlin.context.*
import org.cufy.mmrpc.gen.kotlin.util.annotationClassSpec
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: Context, _: FailScope, _: InitStage)
fun doMetadataDefinitionGen() {
    for (element in ctx.elements) {
        if (element !is MetadataDefinition) continue
        if (!element.isGeneratingClass()) continue

        catch(element) {
            addAnnotationClass(element)
        }
    }
}

context(_: Context, _: InitStage)
private fun addAnnotationClass(element: MetadataDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        <maybe-repeatable-annotation>
        annotation class <name>(
            [
                <property-kdoc>
                [ @<property-metadata> ]
                val <property-name>: <property-type> = <property-default-value>,
            ]
        )
    }
     */

    declareType(
        target = element.namespace,
        declares = listOf(element.canonicalName),
    ) {
        annotationClassSpec(element.nameOfClass()) {
            primaryConstructor(constructorSpec {
                for (field in element.fields) {
                    addParameter(field.metaParameterSpec())
                }
            })

            for (field in element.fields) {
                addProperty(propertySpec(field.nameOfProperty(), field.type.metaTypeName()) {
                    initializer(field.nameOfProperty())

                    addKdoc(createKdocCode(field))

                    for (usage in field.metadata) {
                        addAnnotation(usage.annotationSpec())
                    }
                })
            }

            addKdoc(createKdocCode(element))

            for (usage in element.metadata) {
                addAnnotation(usage.annotationSpec())
            }

            if (element.repeated) {
                addAnnotation(Repeatable::class)
            }

            applyOf(target = element.canonicalName)
        }
    }
}
