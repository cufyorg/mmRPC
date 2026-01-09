package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

@Marker2
interface ConstDefinitionContainerBuilder {
    fun addConstDefinition(value: Unnamed<ConstDefinition>)
}

////////////////////////////////////////

context(ctx: ConstDefinitionContainerBuilder)
operator fun Unnamed<ConstDefinition>.unaryPlus() {
    ctx.addConstDefinition(this)
}

context(ctx: ConstDefinitionContainerBuilder)
operator fun Iterable<Unnamed<ConstDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: ConstDefinitionContainerBuilder)
operator fun ConstDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: ConstDefinitionContainerBuilder)
operator fun Iterable<ConstDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////

context(ctx: ConstDefinitionContainerBuilder)
internal operator fun String.invoke(
    block: ConstDefinitionBlock = {}
) {
    +Unnamed { ns, _ ->
        ConstDefinitionBuilder()
            .also { it.name = this }
            .also { it.namespace = ns }
            .apply(block)
            .build()
    }
}

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    type: TypeDefinition,
    value: Literal = null.literal,
    block: ConstDefinitionBlock = {}
) = this {
    contextOf()
        .also { it.type *= type }
        .also { it.value *= value }
    block()
}

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    type: Unnamed<TypeDefinition>,
    value: Literal = null.literal,
    block: ConstDefinitionBlock = {}
) = this {
    contextOf()
        .also { it.type *= type }
        .also { it.value *= value }
    block()
}

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: NullLiteral,
    block: ConstDefinitionBlock = {}
) = this(builtin.Any.optional, value, block)

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: BooleanLiteral,
    block: ConstDefinitionBlock = {}
) = this(builtin.Boolean, value, block)

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: IntLiteral,
    block: ConstDefinitionBlock = {}
) = this(builtin.Int64, value, block)

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: FloatLiteral,
    block: ConstDefinitionBlock = {}
) = this(builtin.Float64, value, block)

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: StringLiteral,
    block: ConstDefinitionBlock = {}
) = this(builtin.String, value, block)

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: TupleLiteral,
    block: ConstDefinitionBlock = {}
) = this(tuple, value, block)

context(ctx: ConstDefinitionContainerBuilder)
operator fun String.invoke(
    value: StructLiteral,
    block: ConstDefinitionBlock = {}
) = this(struct, value, block)

////////////////////////////////////////
