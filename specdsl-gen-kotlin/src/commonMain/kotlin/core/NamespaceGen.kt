package org.cufy.specdsl.gen.kotlin.core

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import org.cufy.specdsl.Namespace
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asReferenceName
import org.cufy.specdsl.gen.kotlin.util.poet.classOf

class NamespaceGen(override val ctx: GenContext) : GenGroup() {
    fun generateAccessors() {
        for (namespace in ctx.namespaceSet) {
            if (namespace.isOnToplevel) continue

            failGenBoundary {
                generateExtensionAccessor(namespace)
            }
        }
    }

    /**
     * Generate extension namespace shorthand accessor.
     *
     * ### Skipped for:
     *
     * - namespaces that are on toplevel
     *
     * ### Example:
     *
     * ```
     * object custom : NamespaceObject() {
     *      // ...
     *
     *      object example : NamespaceObject(this)
     * }
     * ```
     *
     * ### Produces:
     *
     * ```
     * inline val custom.example get() = custom_example
     * ```
     */
    private fun generateExtensionAccessor(namespace: Namespace) {
        onFileOptional(namespace) {
            val propertyGetterSpec = FunSpec
                .getterBuilder()
                .addModifiers(KModifier.INLINE)
                .addStatement("return %T", classOf(namespace))
                .build()

            val propertySpec = PropertySpec
                .builder(namespace.asReferenceName, classOf(namespace))
                .addKdoc("### namespace ${namespace.canonicalName.value}")
                .receiver(classOf(namespace.parent))
                .getter(propertyGetterSpec)
                .build()

            addProperty(propertySpec)
        }
    }
}
