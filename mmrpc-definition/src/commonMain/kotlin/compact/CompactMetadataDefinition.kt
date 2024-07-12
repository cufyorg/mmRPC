package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("metadata")
data class CompactMetadataDefinition(
    override val name: String = MetadataDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("metadata_fields.ref")
    val metadataFields: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun MetadataDefinition.toCompact(): CompactMetadataDefinition {
    return CompactMetadataDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        metadataFields = this.metadataFields
            .map { it.canonicalName }
    )
}

fun CompactMetadataDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> MetadataDefinition? {
    return it@{
        MetadataDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
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
