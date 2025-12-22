package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberSpecHolder
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.gen.kotlin.*

@Marker3
context(ctx: GenContext)
inline fun failBoundary(block: () -> Unit) {
    try {
        block()
    } catch (e: GenException) {
        ctx.failures += e
    }
}

@Marker3
context(ctx: GenContext)
fun fail(tag: String, definition: ElementDefinition? = null, message: () -> String): Nothing {
    val failure = GenFailure(
        tag = tag,
        message = message(),
        element = definition,
    )

    throw GenException(failure)
}

@Marker3
context(ctx: GenContext)
fun createType(canonicalName: CanonicalName, block: () -> TypeSpec.Builder) {
    ctx.createTypeNodes += CreateTypeNode(canonicalName, block)
}

@Marker3
context(ctx: GenContext)
fun injectType(canonicalName: CanonicalName, block: TypeSpec.Builder.() -> Unit) {
    ctx.injectTypeNodes += InjectTypeNode(canonicalName, block)
}

@Marker3
context(ctx: GenContext)
fun injectFile(canonicalName: CanonicalName?, block: FileSpec.Builder.() -> Unit) {
    ctx.injectFileNodes += InjectFileNode(canonicalName, block)
}

@Marker3
context(ctx: GenContext)
fun injectScope(canonicalName: CanonicalName?, block: MemberSpecHolder.Builder<*>.() -> Unit) {
    ctx.injectScopeNodes += InjectScopeNode(canonicalName, block)
}
