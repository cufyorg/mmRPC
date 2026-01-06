package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.TypeSpec.Companion.annotationBuilder
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

context(ctx: GenContext)
fun consumeMetadataDefinition() {
    for (element in ctx.elements) {
        if (element !is MetadataDefinition) continue
        if (!element.hasGeneratedClass()) continue
        if (element.canonicalName in ctx.ignore) continue

        failBoundary {
            applyCreateAnnotationClass(element)
        }
    }
}

context(ctx: GenContext)
private fun applyCreateAnnotationClass(element: MetadataDefinition) {
    /*
    <namespace> {
        <kdoc>
        [ @<metadata> ]
        annotation class <name>(
            [
                <property-kdoc>
                [ @<property-metadata> ]
                val <property-name>: <property-type> = <property-default-value>,
            ]
        )
    }
     */

    createType(element.canonicalName) {
        annotationBuilder(element.nameOfClass()).apply {
            primaryConstructor(constructorSpec {
                addParameters(element.fields.map {
                    parameterSpec(it.nameOfProperty(), it.type.metaTypeName()) {
                        val default = it.default

                        if (default != null) {
                            defaultValue(createMetaLiteralCode(it.type, default))
                        }
                    }
                })
            })
            addProperties(element.fields.map {
                propertySpec(it.nameOfProperty(), it.type.metaTypeName()) {
                    initializer(it.nameOfProperty())

                    addKdoc(createShortKdocCode(it))
                    addAnnotations(createAnnotationSet(it.metadata))
                }
            })

            addKdoc(createKdocCode(element))
            addAnnotations(createAnnotationSet(element.metadata))
        }
    }
}
