package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.specdsl.MetadataDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.*
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.poet.createLiteralInlinedOrReference
import org.cufy.specdsl.gen.kotlin.util.poet.typeOf

class MetadataDefinitionGen(override val ctx: GenContext) : GenGroup() {
    fun generateClasses() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is MetadataDefinition) continue
            if (element.isAnonymous) continue
            if (element.canonicalName in ctx.nativeElements) continue

            failGenBoundary {
                generateAnnotationClass(element)
            }
        }
    }

    /**
     * Generate annotation classes for metadata definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     * - native elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something__message by metadata_parameter(builtin.String)
     * val custom.example.Something by metadata {
     *     +custom.example.Something__message
     * }
     * ```
     *
     * ### Produces:
     *
     * ```
     * // classes["builtin.String"] = "kotlin.String"
     * // nativeElements += "builtin.String"
     *
     * object custom_example {
     *      // ...
     *
     *      annotation class Something(val message: kotlin.String)
     * }
     * ```
     */
    private fun generateAnnotationClass(element: MetadataDefinition) {
        onObject(element.namespace) {
            val typePrimaryConstructorSpec = FunSpec
                .constructorBuilder()
                .apply {
                    for (it in element.metadataParameters) {
                        val default = it.parameterDefault
                        val parameterSpec = when {
                            default == null -> ParameterSpec
                                .builder(it.name, typeOf(it.parameterType))
                                .build()

                            else -> ParameterSpec
                                .builder(it.name, typeOf(it.parameterType))
                                .defaultValue(createLiteralInlinedOrReference(default))
                                .build()
                        }

                        addParameter(parameterSpec)
                    }
                }
                .build()

            val typeSpec = TypeSpec
                .annotationBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .primaryConstructor(typePrimaryConstructorSpec)
                .apply {
                    for (it in element.metadataParameters) {
                        val propertySpec = PropertySpec
                            .builder(it.name, typeOf(it.parameterType))
                            .addKdoc(createKDoc(it))
                            .addAnnotations(createAnnotationSet(it.metadata))
                            .initializer(it.name)
                            .build()

                        addProperty(propertySpec)
                    }
                }
                .build()

            addType(typeSpec)
        }
    }
}
