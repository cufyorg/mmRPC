package org.cufy.mmrpc.builder

import org.cufy.mmrpc.*

////////////////////////////////////////

typealias FieldUsageBlock = context(FieldUsageBuilder) () -> Unit

context(ctx: FieldUsageBuilder) val builder get() = ctx

@Marker2
class FieldUsageBuilder {
    val definition = Box<FieldDefinition>()
    val value = Box<Literal>()

    fun build(): FieldUsage {
        return FieldUsage(
            definition = this::definition.getOrThrow(),
            value = this::value.getOrThrow(),
        )
    }
}

////////////////////////////////////////

operator fun FieldDefinition.invoke(
    value: Literal,
    block: FieldUsageBlock = {}
): FieldUsage {
    return FieldUsageBuilder()
        .also { it.definition *= this }
        .also { it.value *= value }
        .apply(block)
        .build()
}

////////////////////////////////////////
