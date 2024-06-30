package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.ScalarDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.createKDoc
import org.cufy.specdsl.gen.kotlin.util.poet.*

class ScalarDefinitionGen(override val ctx: GenContext) : GenGroup() {
    fun generateClasses() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is ScalarDefinition) continue
            if (element.isAnonymous) continue
            if (element.canonicalName in ctx.nativeElements) continue

            failGenBoundary {
                generateValueClass(element)
            }
        }
    }

    /**
     * Generate value classes for scalar definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     * - native elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by scalar
     * ```
     *
     * Produces:
     *
     * ```
     * // classes["custom.example.Something"] = "kotlin.String"
     *
     * object custom_example {
     *      // ...
     *
     *      @JvmInline
     *      @Serializable
     *      @SerialName("custom.example.Something")
     *      value class Something(val value: kotlin.String) {
     *          companion object {
     *              const val SERIAL_NAME = "custom.example.Something"
     *          }
     *      }
     * }
     * ```
     */
    private fun generateValueClass(element: ScalarDefinition) {
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
                .addParameter("value", nativeClassOf(element))
                .build()

            val typeValuePropertySpec = PropertySpec
                .builder("value", nativeClassOf(element))
                .initializer("value")
                .build()

            val typeSpec = TypeSpec
                .classBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addAnnotation(JvmInline::class)
                .addModifiers(KModifier.VALUE)
                .addSuperinterfaces(calculateUnionInterfaces(element))
                .primaryConstructor(typePrimaryConstructorSpec)
                .addProperty(typeValuePropertySpec)
                .addType(companionObjectSpec)
                .build()

            addType(typeSpec)
        }
    }
}
