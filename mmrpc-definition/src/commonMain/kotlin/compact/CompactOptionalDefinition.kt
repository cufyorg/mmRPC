package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.OptionalDefinition
import org.cufy.mmrpc.TypeDefinition

@Serializable
@SerialName("optional")
data class CompactOptionalDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("optional_type.ref")
    val optionalType: CanonicalName,
) : CompactElementDefinition

fun OptionalDefinition.toCompact(strip: Boolean = false): CompactOptionalDefinition {
    return CompactOptionalDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        optionalType = this.optionalType.canonicalName,
    )
}

fun CompactOptionalDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> OptionalDefinition? {
    return it@{
        OptionalDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            optionalType = this.optionalType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "optional_type.ref must point to a TypeDefinition"
                }
                item
            }
        )
    }
}
