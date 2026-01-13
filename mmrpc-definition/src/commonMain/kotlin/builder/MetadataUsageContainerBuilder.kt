package org.cufy.mmrpc.builder

import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.MetadataUsage
import org.cufy.mmrpc.timesAssign

////////////////////////////////////////

@Marker2
fun interface MetadataUsageContainerBuilder {
    fun addMetadataUsage(value: MetadataUsage)
}

////////////////////////////////////////

context(ctx: MetadataUsageContainerBuilder)
operator fun MetadataUsage.unaryPlus() {
    ctx.addMetadataUsage(this)
}

context(ctx: MetadataUsageContainerBuilder)
operator fun Iterable<MetadataUsage>.unaryPlus() {
    for (it in this) +it
}

context(ctx: MetadataUsageContainerBuilder)
operator fun MetadataDefinition.unaryPlus() {
    +MetadataUsageBuilder()
        .also { it.definition *= this }
        .build()
}

////////////////////////////////////////
