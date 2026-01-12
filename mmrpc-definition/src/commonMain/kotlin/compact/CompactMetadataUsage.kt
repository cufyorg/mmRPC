package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.MetadataUsage

@Suppress("PropertyName")
@Serializable
data class CompactMetadataUsage(
    val definition_ref: CanonicalName,
    val fields: List<CompactFieldUsage>,
)

fun MetadataUsage.toCompact(): CompactMetadataUsage {
    return CompactMetadataUsage(
        definition_ref = this.definition.canonicalName,
        fields = this.fields.map { it.toCompact() }
    )
}

fun CompactMetadataUsage.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): MetadataUsage? {
    return MetadataUsage(
        definition = this.definition_ref.let {
            val item = onLookup(it) ?: return null
            require(item is MetadataDefinition) {
                "<metadata-usage>.definition_ref must point to a MetadataDefinition"
            }
            item
        },
        fields = this.fields.map {
            it.inflateOrNull(onLookup) ?: return null
        }
    )
}
