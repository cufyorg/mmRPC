package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.Unnamed
import org.cufy.mmrpc.internal.VARARG_VARIANTS_DEPRECATED_MSG

////////////////////////////////////////

@Marker2
interface StructDefinitionContainerBuilder {
    fun addStructDefinition(value: Unnamed<StructDefinition>)
}

////////////////////////////////////////

context(ctx: StructDefinitionContainerBuilder)
operator fun Unnamed<StructDefinition>.unaryPlus() {
    ctx.addStructDefinition(this)
}

context(ctx: StructDefinitionContainerBuilder)
operator fun Iterable<Unnamed<StructDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: StructDefinitionContainerBuilder)
operator fun StructDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: StructDefinitionContainerBuilder)
operator fun Iterable<StructDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////

context(ctx: StructDefinitionContainerBuilder)
operator fun String.invoke(
    block: StructDefinitionBlock = {},
) {
    +Unnamed { ns, _ ->
        StructDefinitionBuilder()
            .also { it.name = this }
            .also { it.namespace = ns }
            .apply(block)
            .build()
    }
}

@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
context(ctx: StructDefinitionContainerBuilder)
operator fun String.invoke(
    vararg fields: FieldDefinition,
    block: StructDefinitionBlock = {},
) = this {
    +fields.asList()
    block()
}

@Deprecated(VARARG_VARIANTS_DEPRECATED_MSG)
context(ctx: StructDefinitionContainerBuilder)
operator fun String.invoke(
    vararg fields: Unnamed<FieldDefinition>,
    block: StructDefinitionBlock = {},
) = this {
    +fields.asList()
    block()
}

////////////////////////////////////////
