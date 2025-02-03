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
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
) : CompactElementDefinition

fun FaultDefinition.toCompact(strip: Boolean = false): CompactFaultDefinition {
    return CompactFaultDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
    )
}

fun CompactFaultDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> FaultDefinition? {
    return it@{
        FaultDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
        )
    }
}
