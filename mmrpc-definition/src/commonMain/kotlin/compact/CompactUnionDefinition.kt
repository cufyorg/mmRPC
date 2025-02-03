package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.UnionDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("union")
data class CompactUnionDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val discriminator: String,
    val types_ref: List<CanonicalName>,
) : CompactElementDefinition

fun UnionDefinition.toCompact(strip: Boolean = false): CompactUnionDefinition {
    return CompactUnionDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
        discriminator = this.discriminator,
        types_ref = this.types.map { it.canonicalName }
    )
}

fun CompactUnionDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> UnionDefinition? {
    return it@{
        UnionDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            discriminator = this.discriminator,
            types = this.types_ref.map {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "<union>.types_ref must point to a StructDefinition"
                }
                item
            },
        )
    }
}
