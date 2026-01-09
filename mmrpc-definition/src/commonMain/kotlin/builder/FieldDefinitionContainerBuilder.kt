package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

@Marker2
interface FieldDefinitionContainerBuilder {
    fun addFieldDefinition(value: Unnamed<FieldDefinition>)
}

////////////////////////////////////////

context(ctx: FieldDefinitionContainerBuilder)
operator fun Unnamed<FieldDefinition>.unaryPlus() {
    ctx.addFieldDefinition(this)
}

context(ctx: FieldDefinitionContainerBuilder)
operator fun Iterable<Unnamed<FieldDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: FieldDefinitionContainerBuilder)
operator fun FieldDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: FieldDefinitionContainerBuilder)
operator fun Iterable<FieldDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////

context(ctx: FieldDefinitionContainerBuilder)
internal operator fun String.invoke(
    block: FieldDefinitionBlock = {},
) {
    +Unnamed { ns, _ ->
        FieldDefinitionBuilder()
            .also { it.name = this }
            .also { it.namespace = ns }
            .apply(block)
            .build()
    }
}

context(ctx: FieldDefinitionContainerBuilder)
operator fun String.invoke(
    type: TypeDefinition,
    block: FieldDefinitionBlock = {},
) = this {
    contextOf()
        .also { it.type *= type }
    block()
}

context(ctx: FieldDefinitionContainerBuilder)
operator fun String.invoke(
    type: Unnamed<TypeDefinition>,
    block: FieldDefinitionBlock = {},
) = this {
    contextOf()
        .also { it.type *= type }
    block()
}

////////////////////////////////////////
