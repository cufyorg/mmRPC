package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FaultDefinition

@Serializable
@SerialName("fault")
data class CompactFaultDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataUsage> = emptyList(),
) : CompactElementDefinition

fun FaultDefinition.toCompact(): CompactFaultDefinition {
    return CompactFaultDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
    )
}

fun CompactFaultDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): FaultDefinition? {
    return FaultDefinition(
        canonicalName = this.canonical_name,
        description = this.description,
        metadata = this.metadata.map {
            it.inflateOrNull(onLookup) ?: return null
        },
    )
}
