package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

typealias RoutineDefinitionBlock = context(RoutineDefinitionBuilder) () -> Unit

@Marker2
class RoutineDefinitionBuilder :
    FaultDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val comm = mutableListOf<Comm>()
    val faults = mutableListOf<Unnamed<FaultDefinition>>()
    internal val input = mutableListOf<context(StructDefinitionBuilder) () -> Unit>()
    internal val output = mutableListOf<context(StructDefinitionBuilder) () -> Unit>()

    override fun addFaultDefinition(value: Unnamed<FaultDefinition>) {
        faults += value
    }

    fun build(): RoutineDefinition {
        val cn = buildCanonicalName()
        return RoutineDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            comm = this.comm.toList(),
            faults = this.faults.mapIndexed { i, it ->
                it.get(cn, name = "fault$i")
            },
            input = this.input.let { blocks ->
                if (blocks.isEmpty()) builtin.Void
                else StructDefinitionBuilder()
                    .also { it.name = "Input" }
                    .also { it.namespace = cn }
                    .apply { for (it in blocks) it() }
                    .build()
            },
            output = this.output.let { blocks ->
                if (blocks.isEmpty()) builtin.Void
                else StructDefinitionBuilder()
                    .also { it.name = "Output" }
                    .also { it.namespace = cn }
                    .apply { for (it in blocks) it() }
                    .build()
            },
        )
    }
}

context(ctx: RoutineDefinitionBuilder)
operator fun Comm.unaryPlus() {
    ctx.comm += this
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
fun input(block: StructDefinitionBlock) {
    ctx.input += block
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
fun output(block: StructDefinitionBlock) {
    ctx.output += block
}

////////////////////////////////////////
