package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.StructDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("inter")
data class CompactInterDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val types_ref: List<CanonicalName>,
) : CompactElementDefinition

fun InterDefinition.toCompact(): CompactInterDefinition {
    return CompactInterDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        types_ref = this.types.map { it.canonicalName },
    )
}

fun CompactInterDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> InterDefinition? {
    return it@{
        InterDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            types = this.types_ref.map {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "<inter>.types_ref must point to a StructDefinition"
                }
                item
            },
        )
    }
}
