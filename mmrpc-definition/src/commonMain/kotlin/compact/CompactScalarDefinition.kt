package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ScalarDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("scalar")
data class CompactScalarDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val type_ref: CanonicalName? = null,
) : CompactElementDefinition

fun ScalarDefinition.toCompact(strip: Boolean = false): CompactScalarDefinition {
    return CompactScalarDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
        type_ref = this.type?.canonicalName,
    )
}

fun CompactScalarDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ScalarDefinition? {
    return it@{
        ScalarDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            type = this.type_ref?.let {
                val item = onLookup(it) ?: return@it null
                require(item is ScalarDefinition) {
                    "<scalar>.type_ref must point to a ScalarDefinition"
                }
                item
            }
        )
    }
}
