package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*
import org.cufy.mmrpc.internal.asSiblingOf

////////////////////////////////////////

typealias ConstDefinitionBlock = context(ConstDefinitionBuilder) () -> Unit

@Marker2
class ConstDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = Box<Unnamed<TypeDefinition>>()
    val value = Box<Literal>()

    fun build(): ConstDefinition {
        val cn = buildCanonicalName()
        return ConstDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this::type.getOrThrow()
                .asSiblingOf(cn, suffix = "_type"),
            value = this::value.getOrThrow(),
        )
    }
}

////////////////////////////////////////

@Marker2
internal fun const(
    block: ConstDefinitionBlock = {}
) = Unnamed { ns, name ->
    ConstDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

@Marker2
fun const(
    type: TypeDefinition,
    value: Literal = null.literal,
    block: ConstDefinitionBlock = {}
) = const {
    contextOf()
        .also { it.type *= type }
        .also { it.value *= value }
    block()
}

@Marker2
fun const(
    type: Unnamed<TypeDefinition>,
    value: Literal = null.literal,
    block: ConstDefinitionBlock = {}
) = const {
    contextOf()
        .also { it.type *= type }
        .also { it.value *= value }
    block()
}

@Marker2
fun const(
    value: NullLiteral,
    block: ConstDefinitionBlock = {}
) = const(builtin.Any.optional, value, block)

@Marker2
fun const(
    value: BooleanLiteral,
    block: ConstDefinitionBlock = {}
) = const(builtin.Boolean, value, block)

@Marker2
fun const(
    value: IntLiteral,
    block: ConstDefinitionBlock = {}
) = const(builtin.Int64, value, block)

@Marker2
fun const(
    value: FloatLiteral,
    block: ConstDefinitionBlock = {}
) = const(builtin.Float64, value, block)

@Marker2
fun const(
    value: StringLiteral,
    block: ConstDefinitionBlock = {}
) = const(builtin.String, value, block)

@Marker2
fun const(
    value: TupleLiteral,
    block: ConstDefinitionBlock = {}
) = const(tuple, value, block)

@Marker2
fun const(
    value: StructLiteral,
    block: ConstDefinitionBlock = {}
) = const(struct, value, block)

////////////////////////////////////////

@Marker2
operator fun TypeDefinition.invoke(
    value: Literal = null.literal,
    block: ConstDefinitionBuilder.() -> Unit = {}
) = const(this, value, block)

@Marker2
operator fun Unnamed<TypeDefinition>.invoke(
    value: Literal = null.literal,
    block: ConstDefinitionBuilder.() -> Unit = {}
) = const(this, value, block)

////////////////////////////////////////
