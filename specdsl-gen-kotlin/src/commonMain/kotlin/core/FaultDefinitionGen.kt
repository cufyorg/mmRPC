package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.FaultDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.createKDoc
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.poet.createOptionalSerialNameAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.poet.createOptionalSerializableAnnotationSet

class FaultDefinitionGen(override val ctx: GenContext) : GenGroup() {
    private val classOfCommonInterface = ClassName(ctx.pkg, "Fault")

    fun generateClasses() {
        generateFaultInterface()

        for (element in ctx.specSheet.collectChildren()) {
            if (element !is FaultDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                generateDataObject(element)
            }
        }
    }

    /**
     * Generate common sealed interface for all fault definitions.
     *
     * Produces:
     *
     * ```
     * // toplevel
     *
     * @Serializable
     * sealed interface Fault
     */
    private fun generateFaultInterface() {
        onToplevel {
            val typePropertySpec = PropertySpec
                .builder("type", STRING)
                .build()

            val typeSpec = TypeSpec
                .interfaceBuilder(classOfCommonInterface)
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addModifiers(KModifier.SEALED)
                .addProperty(typePropertySpec)
                .build()

            addType(typeSpec)
        }
    }

    /**
     * Generate data objects for fault definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by fault
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
     *      data object Something : Fault {
     *          const val SERIAL_NAME = "custom.example.Something"
     *
     *          override val serialName get() = "custom.example.Something"
     *      }
     * }
     * ```
     */
    private fun generateDataObject(element: FaultDefinition) {
        onObject(element.namespace) {
            val serialNameConstantSpec = PropertySpec
                .builder("SERIAL_NAME", STRING)
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.canonicalName.value)
                .build()

            val typePropertySpec = PropertySpec
                .builder("type", STRING)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", element.canonicalName.value)
                .build()

            val typeSpec = TypeSpec
                .objectBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addSuperinterface(classOfCommonInterface)
                .addModifiers(KModifier.DATA)
                .addProperty(serialNameConstantSpec)
                .addProperty(typePropertySpec)
                .build()

            addType(typeSpec)
        }
    }
}
