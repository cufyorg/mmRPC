package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.MetadataDefinition

@Serializable
@SerialName("metadata")
data class CompactMetadataDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("metadata_fields.ref")
    val metadataFields: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun MetadataDefinition.toCompact(strip: Boolean = false): CompactMetadataDefinition {
    return CompactMetadataDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        metadataFields = this.metadataFields
            .map { it.canonicalName }
    )
}

fun CompactMetadataDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> MetadataDefinition? {
    return it@{
        MetadataDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            metadataFields = this.metadataFields.map {
                val item = onLookup(it) ?: return@it null
                require(item is FieldDefinition) {
                    "metadata_fields.ref must point to a FieldDefinition"
                }
                item
            },
        )
    }
}
