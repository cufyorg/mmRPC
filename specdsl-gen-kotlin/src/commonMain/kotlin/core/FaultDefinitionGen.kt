package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import org.cufy.specdsl.FaultDefinition
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asReferenceName
import org.cufy.specdsl.gen.kotlin.util.poet.createAnnotationSet
import org.cufy.specdsl.gen.kotlin.util.createKDoc

class FaultDefinitionGen(override val ctx: GenContext) : GenGroup() {
    fun generateConstants() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is FaultDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                generateCanonicalNameConstant(element)
            }
        }
    }

    /**
     * Generate fields for the canonical names of fault definitions.
     *
     * ### Skipped for:
     *
     * - anonymous elements
     *
     * ### Example:
     *
     * ```
     * val custom.example.Something by fault
     * ```
     *
     * ### Produces:
     *
     * ```
     * inline val custom_example.Something: String get() = "custom.example.Something"
     * ```
     */
    private fun generateCanonicalNameConstant(element: FaultDefinition) {
        onObject(element.namespace) {
            val propertySpec = PropertySpec
                .builder(element.asReferenceName, STRING)
                .addKdoc(createKDoc(element))
                .addAnnotations(createAnnotationSet(element.metadata))
                .addModifiers(KModifier.CONST)
                .initializer("%S", element.canonicalName.value)
                .build()

            addProperty(propertySpec)
        }
    }
}
