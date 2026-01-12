package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.TraitDefinition
import org.cufy.mmrpc.Unnamed
import kotlin.jvm.JvmName

////////////////////////////////////////

@Marker2
interface TraitDefinitionContainerBuilder {
    fun addTraitDefinition(value: Unnamed<TraitDefinition>)
}

////////////////////////////////////////

context(ctx: TraitDefinitionContainerBuilder)
operator fun Unnamed<TraitDefinition>.unaryPlus() {
    ctx.addTraitDefinition(this)
}

@JvmName("Iterable_Unnamed_TraitDefinition_unaryPlus")
context(ctx: TraitDefinitionContainerBuilder)
operator fun Iterable<Unnamed<TraitDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: TraitDefinitionContainerBuilder)
operator fun TraitDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: TraitDefinitionContainerBuilder)
operator fun Iterable<TraitDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////
