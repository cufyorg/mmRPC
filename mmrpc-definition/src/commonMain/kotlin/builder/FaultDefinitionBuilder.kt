package org.cufy.mmrpc.builder

import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.Marker2
import org.cufy.mmrpc.Unnamed

////////////////////////////////////////

typealias FaultDefinitionBlock = context(FaultDefinitionBuilder) () -> Unit

@Marker2
class FaultDefinitionBuilder :
    ElementDefinitionBuilder() {
    fun build(): FaultDefinition {
        val cn = buildCanonicalName()
        return FaultDefinition(
            canonicalName = cn,
            description = buildDescription(),
            metadata = buildMetadata(),
        )
    }
}

////////////////////////////////////////

@Marker2
val fault = fault()

@Marker2
fun fault(
    block: FaultDefinitionBlock = {},
) = Unnamed { ns, name ->
    FaultDefinitionBuilder()
        .also { it.name = name ?: return@also }
        .also { it.namespace = ns }
        .apply(block)
        .build()
}

////////////////////////////////////////
