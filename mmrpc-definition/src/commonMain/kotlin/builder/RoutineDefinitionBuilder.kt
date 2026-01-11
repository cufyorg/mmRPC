package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

typealias RoutineDefinitionBlock = context(RoutineDefinitionBuilder) () -> Unit

@Marker2
class RoutineDefinitionBuilder :
    FaultDefinitionContainerBuilder,
    ElementDefinitionBuilder() {
    val faults = mutableListOf<Unnamed<FaultDefinition>>()
    internal var inputShape: Comm.Shape? = null
    internal var outputShape: Comm.Shape? = null
    internal val input = mutableListOf<context(StructDefinitionBuilder) () -> Unit>()
    internal val output = mutableListOf<context(StructDefinitionBuilder) () -> Unit>()

    override fun addFaultDefinition(value: Unnamed<FaultDefinition>) {
        faults += value
    }

    fun build(): RoutineDefinition {
        val comm = Comm.of(this.inputShape, this.outputShape)
            ?: error("Cannot get Comm from input=$inputShape output=$outputShape")

        check(comm.input != Comm.Shape.Void || input.isEmpty()) {
            "Injected input cannot be delivered when input shape is Void"
        }
        check(comm.output != Comm.Shape.Void || output.isEmpty()) {
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

@Marker0
context(ctx: RoutineDefinitionBuilder)
operator fun Comm.unaryPlus() {
    check(ctx.inputShape == null || ctx.inputShape == this.input) {
        "Input shape conflict: Shape was already set to ${ctx.inputShape}"
    }
    check(ctx.outputShape == null || ctx.outputShape == this.output) {
        "Output shape conflict: Shape was already set to ${ctx.outputShape}"
    }

    ctx.inputShape = this.input
    ctx.outputShape = this.output
}

////////////////////////////////////////

data object UnaryCommShapeKeyword

@Marker0
context(_: RoutineDefinitionBuilder)
val unary get() = UnaryCommShapeKeyword

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun UnaryCommShapeKeyword.input(block: StructDefinitionBlock) {
    check(ctx.inputShape == null || ctx.inputShape == Comm.Shape.Unary) {
        "Input shape conflict: Shape was already set to ${ctx.inputShape}"
    }

    ctx.input += block
    ctx.inputShape = Comm.Shape.Unary
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun UnaryCommShapeKeyword.output(block: StructDefinitionBlock) {
    check(ctx.outputShape == null || ctx.outputShape == Comm.Shape.Unary) {
        "Output shape conflict: Shape was already set to ${ctx.outputShape}"
    }

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
    check(ctx.inputShape == null || ctx.inputShape == Comm.Shape.Stream) {
        "Input shape conflict: Shape was already set to ${ctx.inputShape}"
    }

    ctx.input += block
    ctx.inputShape = Comm.Shape.Stream
}

@Marker0
context(ctx: RoutineDefinitionBuilder)
infix fun StreamCommShapeKeyword.output(block: StructDefinitionBlock) {
    check(ctx.outputShape == null || ctx.outputShape == Comm.Shape.Stream) {
        "Output shape conflict: Shape was already set to ${ctx.outputShape}"
    }

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
