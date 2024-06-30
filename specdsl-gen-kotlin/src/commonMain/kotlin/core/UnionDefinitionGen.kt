package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.ConstDefinition
import org.cufy.specdsl.UnionDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.UnionStrategy
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.calculateUnionStrategy
import org.cufy.specdsl.gen.kotlin.util.createKDoc
import org.cufy.specdsl.gen.kotlin.util.poet.*

class UnionDefinitionGen(override val ctx: GenContext) : GenGroup() {
    fun generateClasses() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is UnionDefinition) continue
            if (element.isAnonymous) continue
            if (element.unionTypes.isEmpty()) continue

            failGenBoundary {
                when (calculateUnionStrategy(element)) {
                    UnionStrategy.DATA_OBJECT -> generateDataObject(element)
                    UnionStrategy.ENUM_CLASS -> generateEnumClass(element)
                    UnionStrategy.SEALED_INTERFACE -> generateSealedInterface(element)
                    UnionStrategy.WRAPPER_CLASS -> generateWrapperClass(element)
                }
            }
        }
    }

    /**
     * Generate data objects for union definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     * - elements with types
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by union
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
    private fun generateDataObject(element: UnionDefinition) {
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
                .addProperty(serialNameConstantSpec)
                .build()

            addType(typeSpec)
        }
    }

    /**
     * Generate enum classes from union definitions.
     *
     * ### Skipped for:
     *
     * - anonymous elements
     * - elements with no types
     * - elements with any non-const type
     * - elements with inconsistent const types
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something__OPEN by const(builtin.String, "open".literal)
     * val custom.example.Something__CLOSE by const(builtin.String, "close".literal)
     * val custom.example.Something by union {
     *     +custom.example.Something__OPEN
     *     +custom.example.Something__CLOSE
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
     *      @Serializable
     *      @SerialName("custom.example.Something")
     *      enum class Something(val value: kotlin.String) {
     *          @SerialName("open")
     *          OPEN(custom.example.Something.OPEN),
     *
     *          @SerialName("close")
     *          CLOSE(custom.example.Something.CLOSE),
     *
     *          ;
     *
     *          companion object {
     *              const val SERIAL_NAME = "custom.example.Something"
     *          }
     *      }
     * }
     * ```
     */
    private fun generateEnumClass(element: UnionDefinition) {
        @Suppress("UNCHECKED_CAST")
        val unionTypes = element.unionTypes as List<ConstDefinition>
        val commonType = unionTypes[0].constType

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
                .addParameter("value", typeOf(commonType))
                .build()

            val typeValuePropertySpec = PropertySpec
                .builder("value", typeOf(commonType))
                .initializer("value")
                .build()

            val typeSpec = TypeSpec
                .enumBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .primaryConstructor(typePrimaryConstructorSpec)
                .addProperty(typeValuePropertySpec)
                .addType(companionObjectSpec)
                .apply {
                    for (it in unionTypes) {
                        val typeSpec = TypeSpec
                            .anonymousClassBuilder()
                            .addKdoc(createKDoc(it))
                            .addAnnotations(createAnnotationSet(it.metadata))
                            // TODO is this ok? why not element.canonicalName.value?
                            .addAnnotations(createOptionalSerialNameAnnotationSet(createLiteralInlinedOrReference(it)))
                            .addSuperclassConstructorParameter(createLiteralOrReference(it))
                            .build()

                        addEnumConstant(it.name, typeSpec)
                    }
                }
                .build()

            addType(typeSpec)
        }
    }

    /**
     * Generate sealed interfaces from union definitions.
     *
     * ### Skipped for:
     *
     * - anonymous elements
     * - elements with no types
     * - elements with any const type
     * - elements with any optional type
     * - elements with any array type
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something0 by struct
     * val custom.example.Something1 by struct
     * val custom.example.Something by union {
     *     +custom.example.Something0
     *     +custom.example.Something1
     * }
     * ```
     *
     * ### Produces:
     *
     * ```
     * object custom_example {
     *      // ...
     *
     *      @Serializable
     *      @SerialName("custom.example.Something")
     *      sealed interface Something(val value: kotlin.String) {
     *          companion object {
     *              const val SERIAL_NAME = "custom.example.Something"
     *          }
     *      }
     * }
     * ```
     */
    private fun generateSealedInterface(element: UnionDefinition) {
        val unionTypes = element.unionTypes

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

            val typeSpec = TypeSpec
                .interfaceBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addModifiers(KModifier.SEALED)
                .addSuperinterfaces(calculateUnionInterfaces(element))
                .addType(companionObjectSpec)
                .build()

            addType(typeSpec)
        }
    }

    /**
     * Generate sealed wrapper classes from union definitions.
     *
     * ### Skipped for:
     *
     * - anonymous elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something0 by struct
     * val custom.example.Something1 by struct
     * val custom.example.Something by union {
     *     +custom.example.Something0
     *     +custom.example.Something1
     * }
     * ```
     *
     * ### Produces:
     *
     * ```
     * // classes["builtin.String"] = "kotlin.String"
     * // nativeElements += "builtin.String"
     * // packageName = "specdsl"
     *
     * object custom_example {
     *      // ...
     *
     *      @Serializable
     *      @SerialName("custom.example.Something")
     *      sealed class Something(val value: kotlin.String) {
     *          @Serializable
     *          @SerialName("custom.example.Something0")
     *          data class Something0(val value: specdsl.Custom_Example.Something0) : Something
     *
     *          @Serializable
     *          @SerialName("custom.example.Something1")
     *          data class Something1(val value: specdsl.Custom_Example.Something1) : Something
     *
     *          companion object {
     *              const val SERIAL_NAME = "custom.example.Something"
     *          }
     *      }
     * }
     * ```
     */
    private fun generateWrapperClass(element: UnionDefinition) {
        val unionTypes = element.unionTypes

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

            val typeSpec = TypeSpec
                .classBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addModifiers(KModifier.SEALED)
                .addSuperinterfaces(calculateUnionInterfaces(element))
                .addType(companionObjectSpec)
                .apply {
                    for (it in unionTypes) {
                        val typePrimaryConstructorSpec = FunSpec
                            .constructorBuilder()
                            .addParameter("value", typeOf(it))
                            .build()

                        val typeValuePropertySpec = PropertySpec
                            .builder("value", typeOf(it))
                            .initializer("value")
                            .build()

                        val typeSpec = TypeSpec
                            .classBuilder(it.name)
                            .superclass(classOf(element))
                            .addKdoc(createKDoc(it))
                            .addAnnotations(createAnnotationSet(it.metadata))
                            .addAnnotations(createOptionalSerializableAnnotationSet())
                            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                            .primaryConstructor(typePrimaryConstructorSpec)
                            .addProperty(typeValuePropertySpec)
                            .addModifiers(KModifier.DATA)
                            .build()

                        addType(typeSpec)
                    }
                }
                .build()

            addType(typeSpec)
        }
    }
}
