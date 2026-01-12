package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.MetadataDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("metadata")
data class CompactMetadataDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataUsage> = emptyList(),

    val fields_ref: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun MetadataDefinition.toCompact(): CompactMetadataDefinition {
    return CompactMetadataDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        fields_ref = this.fields.map { it.canonicalName }
    )
}

fun CompactMetadataDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): MetadataDefinition? {
    return MetadataDefinition(
        canonicalName = this.canonical_name,
        description = this.description,
        metadata = this.metadata.map {
            it.inflateOrNull(onLookup) ?: return null
        },
        fields = this.fields_ref.map {
            val item = onLookup(it) ?: return null
            require(item is FieldDefinition) {
                "<metadata>.fields_ref must point to a FieldDefinition"
            }
            item
        },
    )
}
