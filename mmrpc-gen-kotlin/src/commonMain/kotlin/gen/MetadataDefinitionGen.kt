package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.TypeSpec.Companion.annotationBuilder
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.constructorSpec
import org.cufy.mmrpc.gen.kotlin.util.parameterSpec
import org.cufy.mmrpc.gen.kotlin.util.propertySpec

class MetadataDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is MetadataDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateAnnotationClass(element)
            }
        }
    }

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
            annotationBuilder(asClassName(element)).apply {
                primaryConstructor(constructorSpec {
                    addParameters(element.fields.map {
                        parameterSpec(asPropertyName(it), metaClassOf(it.type)) {
                            val default = it.default

                            if (default != null) {
                                defaultValue(createMetaLiteral(it.type, default))
                            }
                        }
                    })
                })
                addProperties(element.fields.map {
                    propertySpec(asPropertyName(it), metaClassOf(it.type)) {
                        initializer(asPropertyName(it))

                        addKdoc(createKDocShort(it))
                        addAnnotations(createAnnotationSet(it.metadata))
                    }
                })

                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
            }
        }
    }
}
