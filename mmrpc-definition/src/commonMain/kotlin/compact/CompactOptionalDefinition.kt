package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.OptionalDefinition
import org.cufy.mmrpc.TypeDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("optional")
data class CompactOptionalDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val type_ref: CanonicalName,
) : CompactElementDefinition

fun OptionalDefinition.toCompact(): CompactOptionalDefinition {
    return CompactOptionalDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        type_ref = this.type.canonicalName,
    )
}

fun CompactOptionalDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> OptionalDefinition? {
    return it@{
        OptionalDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            type = this.type_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "<optional>.type_ref must point to a TypeDefinition"
                }
                item
            }
        )
    }
}
