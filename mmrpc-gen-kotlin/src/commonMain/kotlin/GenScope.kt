package org.cufy.mmrpc.gen.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberSpecHolder
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3

abstract class GenScope {
    abstract val ctx: GenContext

    abstract fun apply()

    @Marker3
    inline fun failBoundary(block: () -> Unit) {
        try {
            block()
        } catch (e: GenException) {
            ctx.failures += e
        }
    }

    @Marker3
    fun fail(tag: String, definition: ElementDefinition? = null, message: () -> String): Nothing {
        val failure = GenFailure(
            group = this::class.simpleName.orEmpty(),
            tag = tag,
            message = message(),
            element = definition,
        )

        throw GenException(failure)
    }

    @Marker3
    fun createType(canonicalName: CanonicalName, block: () -> TypeSpec.Builder) {
        ctx.createTypeNodes += CreateTypeNode(canonicalName, block)
    }

    @Marker3
    fun injectType(canonicalName: CanonicalName, block: TypeSpec.Builder.() -> Unit) {
        ctx.injectTypeNodes += InjectTypeNode(canonicalName, block)
    }

    @Marker3
    fun injectFile(canonicalName: CanonicalName, block: FileSpec.Builder.() -> Unit) {
        ctx.injectFileNodes += InjectFileNode(canonicalName, block)
    }

    @Marker3
    fun injectScope(canonicalName: CanonicalName?, block: MemberSpecHolder.Builder<*>.() -> Unit) {
        ctx.injectScopeNodes += InjectScopeNode(canonicalName, block)
    }
}
