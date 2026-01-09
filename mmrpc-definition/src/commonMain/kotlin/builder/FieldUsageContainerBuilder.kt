package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FieldUsage
import org.cufy.mmrpc.Marker2

////////////////////////////////////////

@Marker2
interface FieldUsageContainerBuilder {
    fun addFieldUsage(value: FieldUsage)
}

////////////////////////////////////////

context(ctx: FieldUsageContainerBuilder)
operator fun FieldUsage.unaryPlus() {
    ctx.addFieldUsage(this)
}

context(ctx: FieldUsageContainerBuilder)
operator fun Iterable<FieldUsage>.unaryPlus() {
    for (it in this) +it
}

////////////////////////////////////////
