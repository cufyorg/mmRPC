package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.Unnamed
import kotlin.jvm.JvmName

////////////////////////////////////////

@Marker2
interface FaultDefinitionContainerBuilder {
    fun addFaultDefinition(value: Unnamed<FaultDefinition>)
}

////////////////////////////////////////

context(ctx: FaultDefinitionContainerBuilder)
operator fun Unnamed<FaultDefinition>.unaryPlus() {
    ctx.addFaultDefinition(this)
}

@JvmName("Iterable_Unnamed_FaultDefinition_unaryPlus")
context(ctx: FaultDefinitionContainerBuilder)
operator fun Iterable<Unnamed<FaultDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: FaultDefinitionContainerBuilder)
operator fun FaultDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: FaultDefinitionContainerBuilder)
operator fun Iterable<FaultDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////
