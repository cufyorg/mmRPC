package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.util.gen.debug
import org.cufy.mmrpc.gen.kotlin.util.gen.references.asClassName

abstract class GenGroup {
    abstract val ctx: GenContext

    abstract fun apply()

    @Marker3
    inline fun failGenBoundary(block: () -> Unit) {
        try {
            block()
        } catch (e: GenException) {
            ctx.failures += e
        }
    }

    @Marker3
    fun failGen(tag: String, definition: ElementDefinition, message: () -> String): Nothing {
        val failure = GenFailure(
            group = this::class.simpleName.orEmpty(),
            tag = tag,
            message = message(),
            element = definition,
        )

        throw GenException(failure)
    }

    @Marker3
    fun create(element: ElementDefinition, block: () -> TypeSpec.Builder) {
        debug {
            if (ctx.createElementNodes.any { it.element == element })
                failGen("ctx.create", element) { "Element was registered twice." }
        }

        ctx.createElementNodes += CreateElementNode(
            element = element,
            block = block,
        )
    }

    @Marker3
    fun createObject(element: ElementDefinition, block: TypeSpec.Builder.() -> Unit) {
        create(element) {
            TypeSpec.objectBuilder(asClassName(element))
                .apply(block)
        }
    }

    @Marker3
    fun createEnum(element: ElementDefinition, block: TypeSpec.Builder.() -> Unit) {
        create(element) {
            TypeSpec.enumBuilder(asClassName(element))
                .apply(block)
        }
    }

    @Marker3
    fun createClass(element: ElementDefinition, block: TypeSpec.Builder.() -> Unit) {
        create(element) {
            TypeSpec.classBuilder(asClassName(element))
                .apply(block)
        }
    }

    @Marker3
    fun createInterface(element: ElementDefinition, block: TypeSpec.Builder.() -> Unit) {
        create(element) {
            TypeSpec.interfaceBuilder(asClassName(element))
                .apply(block)
        }
    }

    @Marker3
    fun createAnnotation(element: ElementDefinition, block: TypeSpec.Builder.() -> Unit) {
        create(element) {
            TypeSpec.annotationBuilder(asClassName(element))
                .apply(block)
        }
    }

    @Marker3
    fun on(element: ElementDefinition, block: TypeSpec.Builder.() -> Unit) {
        ctx.onElementNodes += OnElementNode(
            element = element,
            block = block,
        )
    }
}
