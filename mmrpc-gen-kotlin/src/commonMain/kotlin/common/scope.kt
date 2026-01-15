package org.cufy.mmrpc.gen.kotlin.common

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberSpecHolder
import com.squareup.kotlinpoet.TypeSpec
import org.cufy.mmrpc.*
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

@Marker3
context(ctx: GenContext)
fun CanonicalName.resolveRoot(): CanonicalName? {
    // Return the namespace of the top most element this element is on.

    var pkg = namespace ?: return null

    while (pkg !in ctx.roots)
        pkg = pkg.namespace ?: return null

    return pkg
}

@Marker3
context(ctx: GenContext)
fun CanonicalName.resolveElement(): ElementDefinition? =
    ctx.elementsMap[this]

@Marker3
context(ctx: GenContext)
fun ElementDefinition.resolveParent(): ElementDefinition? =
    namespace?.resolveElement()

/**
 * Return a human-readable name of the given [this].
 */
fun ElementDefinition.humanSignature(): String {
    val discriminator = when (this) {
        is ArrayDefinition -> "array"
        is MapDefinition -> "map"
        is EnumDefinition -> "enum"
        is ConstDefinition -> "const"
        is FaultDefinition -> "fault"
        is FieldDefinition -> "field"
        is InterDefinition -> "inter"
        is MetadataDefinition -> "metadata"
        is OptionalDefinition -> "optional"
        is ProtocolDefinition -> "protocol"
        is RoutineDefinition -> "routine"
        is ScalarDefinition -> "scalar"
        is TraitDefinition -> "trait"
        is StructDefinition -> "struct"
        is TupleDefinition -> "tuple"
        is UnionDefinition -> "union"
    }

    return "$discriminator ${canonicalName.value}"
}

context(ctx: GenContext)
fun TraitDefinition.collectStructs(): List<StructDefinition> {
    return ctx.elements.asSequence()
        .filterIsInstance<StructDefinition>()
        .filter { this in it.traits }
        .toList()
}

fun StructDefinition.fieldsInherited() =
    traits.flatMap { it.fieldsInherited() + it.fields }.distinct()

fun TraitDefinition.fieldsInherited(): List<FieldDefinition> =
    traits.flatMap { it.fieldsInherited() + it.fields }.distinct()
