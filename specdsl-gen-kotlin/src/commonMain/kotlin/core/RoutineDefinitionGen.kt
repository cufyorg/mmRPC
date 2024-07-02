package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import org.cufy.specdsl.RoutineDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.createKDoc
import org.cufy.specdsl.gen.kotlin.util.poet.classOf
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.poet.createOptionalSerialNameAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.poet.createOptionalSerializableAnnotationSet

data class RoutineDefinitionGen(override val ctx: GenContext) : GenGroup() {
    private val classOfRoutineInterface = ClassName(ctx.pkg, "Routine")

    fun generateClasses() {
        generateRoutineInterface()

        for (element in ctx.specSheet.collectChildren()) {
            if (element !is RoutineDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                generateDataObject(element)
            }
        }
    }

    /**
     * Generate common sealed interface for all routine definitions.
     *
     * Produces:
     *
     * ```
     * // toplevel
     *
     * @Serializable
     * sealed interface Routine
     */
    private fun generateRoutineInterface() {
        onToplevel {
            val typePropertySpec = PropertySpec
                .builder("type", STRING)
                .build()

            val typeSpec = TypeSpec
                .interfaceBuilder(classOfRoutineInterface)
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addModifiers(KModifier.SEALED)
                .addProperty(typePropertySpec)
                .addTypeVariable(TypeVariableName("I", ANY))
                .addTypeVariable(TypeVariableName("O", ANY))
                .build()

            addType(typeSpec)
        }
    }

    /**
     * Generate data objects for routine definitions.
     *
     * ### Skip for:
     *
     * - anonymous elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by routine
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
     *      data object Something : Routine {
     *          const val SERIAL_NAME = "custom.example.Something"
     *
     *          override val serialName get() = "custom.example.Something"
     *      }
     * }
     * ```
     */
    private fun generateDataObject(element: RoutineDefinition) {
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

            val classOfRoutineInterfaceWithParameters = classOfRoutineInterface
                .plusParameter(classOf(element.asNamespace).nestedClass("Input"))
                .plusParameter(classOf(element.asNamespace).nestedClass("Output"))

            val typeSpec = TypeSpec
                .objectBuilder(element.asClassName)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addAnnotations(createOptionalSerializableAnnotationSet())
                .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
                .addSuperinterface(classOfRoutineInterfaceWithParameters)
                .addModifiers(KModifier.DATA)
                .addProperty(serialNameConstantSpec)
                .addProperty(typePropertySpec)
                .build()

            addType(typeSpec)
        }
    }
}
