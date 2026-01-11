package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

typealias RoutineDefinitionBlock = context(RoutineDefinitionBuilder) () -> Unit

@Marker2
class RoutineDefinitionBuilder :
    FaultDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val faults = mutableListOf<Unnamed<FaultDefinition>>()
    var inputShape = Comm.Shape.Void
    var outputShape = Comm.Shape.Void
    val input = mutableListOf<context(StructDefinitionBuilder) () -> Unit>()
    val output = mutableListOf<context(StructDefinitionBuilder) () -> Unit>()

    override fun addFaultDefinition(value: Unnamed<FaultDefinition>) {
        faults += value
    }

    fun build(): RoutineDefinition {
        check(inputShape != Comm.Shape.Void || input.isEmpty()) {
            "Injected input cannot be delivered when input shape is Void"
        }
        check(outputShape != Comm.Shape.Void || output.isEmpty()) {
            "Injected output cannot be delivered when output shape is Void"
        }

        val cn = buildCanonicalName()
        return RoutineDefinition(
            canonicalName = cn,
            description = this.description,
            metadata = this.metadata.toList(),
            comm = Comm.of(this.inputShape, this.outputShape)
                ?: error("Cannot select Comm mode from input=$inputShape output=$outputShape"),
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

////////////////////////////////////////

data object UnaryCommShapeKeyword

@Marker0
context(_: RoutineDefinitionBuilder)
val unary get() = UnaryCommShapeKeyword

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun UnaryCommShapeKeyword.input(block: StructDefinitionBlock) {
    ctx.input += block
    ctx.inputShape = Comm.Shape.Unary
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun UnaryCommShapeKeyword.output(block: StructDefinitionBlock) {
    ctx.output += block
    ctx.outputShape = Comm.Shape.Unary
}

////////////////////////////////////////

data object StreamCommShapeKeyword

@Marker0
context(_: RoutineDefinitionBuilder)
val stream get() = StreamCommShapeKeyword

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun StreamCommShapeKeyword.input(block: StructDefinitionBlock) {
    ctx.input += block
    ctx.inputShape = Comm.Shape.Stream
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun StreamCommShapeKeyword.output(block: StructDefinitionBlock) {
    ctx.output += block
    ctx.outputShape = Comm.Shape.Stream
}

////////////////////////////////////////

@Marker0
context(ctx: RoutineDefinitionBuilder)
fun injectInput(block: StructDefinitionBlock) {
    ctx.input += block
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
fun injectOutput(block: StructDefinitionBlock) {
    ctx.output += block
}

////////////////////////////////////////
