package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.Unnamed
import kotlin.jvm.JvmName

////////////////////////////////////////

@Marker2
interface RoutineDefinitionContainerBuilder {
    fun addRoutineDefinition(value: Unnamed<RoutineDefinition>)
}

////////////////////////////////////////

context(ctx: RoutineDefinitionContainerBuilder)
operator fun Unnamed<RoutineDefinition>.unaryPlus() {
    ctx.addRoutineDefinition(this)
}

@JvmName("Iterable_Unnamed_RoutineDefinition_unaryPlus")
context(ctx: RoutineDefinitionContainerBuilder)
operator fun Iterable<Unnamed<RoutineDefinition>>.unaryPlus() {
    for (it in this) +it
}

context(ctx: RoutineDefinitionContainerBuilder)
operator fun RoutineDefinition.unaryPlus() {
    +Unnamed(this)
}

context(ctx: RoutineDefinitionContainerBuilder)
operator fun Iterable<RoutineDefinition>.unaryPlus() {
    for (it in this) +Unnamed(it)
}

////////////////////////////////////////

context(ctx: RoutineDefinitionContainerBuilder)
operator fun String.invoke(
    block: RoutineDefinitionBlock
) {
    +Unnamed { ns, _ ->
        RoutineDefinitionBuilder()
            .also { it.name = this }
            .also { it.namespace = ns }
            .apply(block)
            .build()
    }
}

context(ctx: RoutineDefinitionContainerBuilder)
operator fun String.invoke(
    comm: Comm,
    block: RoutineDefinitionBlock
) {
    this {
        contextOf()
            .also { it.inputShape = comm.input }
            .also { it.outputShape = comm.output }
        block()
    }
}

////////////////////////////////////////
