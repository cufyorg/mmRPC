package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.StructDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.StructStrategy
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.calculateStructStrategy
import org.cufy.specdsl.gen.kotlin.util.createKDoc
import org.cufy.specdsl.gen.kotlin.util.poet.*

class StructDefinitionGen(override val ctx: GenContext) : GenGroup() {
    fun generateClasses() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is StructDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                when (calculateStructStrategy(element)) {
                    StructStrategy.DATA_OBJECT -> generateDataObject(element)
                    StructStrategy.DATA_CLASS -> generateDataClass(element)
                }
            }
        }
    }

    /**
     * Generate data objects for struct definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     * - elements with fields
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by struct
     * ```
     *
     * Produces:
     *
     * ```
     * object custom_example {
     *      // ...
     *
     *      @Serializable
     *      @SerialName("custom.example.Something")
     *      data object Something {
     *          const val SERIAL_NAME = "custom.example.Something"
     *      }
     * }
     * ```
     */
    private fun generateDataObject(element: StructDefinition) {
        onObject(element.namespace) {
            val serialNameConstantSpec = PropertySpec
                .builder("SERIAL_NAME", STRING)
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.canonicalName.value)
                .build()

            val typeSpec = TypeSpec
                .objectBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addModifiers(KModifier.DATA)
                .addSuperinterfaces(calculateUnionInterfaces(element))
                .addProperty(serialNameConstantSpec)
                .build()

            addType(typeSpec)
        }
    }

    /**
     * Generate data classes for struct definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     * - elements with no fields
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by struct {
     *      "id"(builtin.String)
     *      "timestamp"(builtin.Int64)
     * }
     * ```
     *
     * Produces:
     *
     * ```
     * // classes["builtin.String"] = "kotlin.String"
     * // classes["builtin.Int64"] = "kotlin.Long"
     *
     * object custom_example {
     *      // ...
     *
     *      @Serializable
     *      @SerialName("custom.example.Something")
     *      data class Something(
     *          @SerialName("message")
     *          val message: kotlin.String,
     *          @SerialName("timestamp")
     *          val timestamp: kotlin.Long,
     *      ) {
     *          companion object {
     *              const val SERIAL_NAME = "custom.example.Something"
     *          }
     *      }
     * }
     * ```
     */
    private fun generateDataClass(element: StructDefinition) {
        onObject(element.namespace) {
            val serialNameConstantSpec = PropertySpec
                .builder("SERIAL_NAME", STRING)
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.canonicalName.value)
                .build()

            val companionObjectSpec = TypeSpec
                .companionObjectBuilder()
                .addProperty(serialNameConstantSpec)
                .build()

            val typePrimaryConstructorSpec = FunSpec
                .constructorBuilder()
                .apply {
                    for (field in element.structFields) {
                        val parameterSpec = ParameterSpec
                            .builder(field.name, typeOf(field.fieldType))
                            .apply {
                                field.fieldDefault?.let { fieldDefault ->
                                    defaultValue(createLiteralOrReference(fieldDefault))
                                }
                            }
                            .build()

                        addParameter(parameterSpec)
                    }
                }
                .build()

            val typeSpec = TypeSpec
                .classBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addModifiers(KModifier.DATA)
                .addSuperinterfaces(calculateUnionInterfaces(element))
                .primaryConstructor(typePrimaryConstructorSpec)
                .addType(companionObjectSpec)
                .apply {
                    for (field in element.structFields) {
                        val propertySpec = PropertySpec
                            .builder(field.name, typeOf(field.fieldType))
                            .addKdoc(createKDoc(field))
                            .addAnnotations(createAnnotationSet(field.metadata))
                            // fixme nameOrReferenceOf to account for anonymous fields
                            .addAnnotations(createOptionalSerialNameAnnotationSet(referenceOf(field)))
                            .initializer("%L", field.name)
                            .build()

                        addProperty(propertySpec)
                    }
                }
                .build()

            addType(typeSpec)
        }
    }
}
