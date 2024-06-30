package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import org.cufy.specdsl.FieldDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asReferenceName
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.createKDoc

class FieldDefinitionGen(override val ctx: GenContext) : GenGroup() {
    fun generateConstants() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is FieldDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                generateNameConstant(element)
            }
        }
    }

    /**
     * Generate fields for the names of field definitions.
     *
     * ### Skipped for:
     *
     * - anonymous elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.something by prop(builtin.String)
     * ```
     *
     * ### Produces:
     *
     * ```
     * inline val custom_example.SOMETHING: String get() = "something"
     * ```
     */
    private fun generateNameConstant(element: FieldDefinition) {
        onObject(element.namespace) {
            val propertySpec = PropertySpec
                .builder(element.asReferenceName, STRING)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.name)
                .build()

            addProperty(propertySpec)
        }
    }
}
