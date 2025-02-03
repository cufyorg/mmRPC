package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.MetadataDefinition
import org.cufy.mmrpc.MetadataDefinitionUsage

@Suppress("PropertyName")
@Serializable
data class CompactMetadataDefinitionUsage(
    val definition_ref: CanonicalName,
    val fields: List<CompactFieldDefinitionUsage>,
)

fun MetadataDefinitionUsage.toCompact(strip: Boolean = false): CompactMetadataDefinitionUsage {
    return CompactMetadataDefinitionUsage(
        definition_ref = this.definition.canonicalName,
        fields = this.fields.map { it.toCompact(strip) }
    )
}

fun CompactMetadataDefinitionUsage.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> MetadataDefinitionUsage? {
    return it@{
        MetadataDefinitionUsage(
            definition = this.definition_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is MetadataDefinition) {
                    "<metadata-usage>.definition_ref must point to a MetadataDefinition"
                }
                item
            },
            fields = this.fields.map {
                it.inflate(onLookup)() ?: return@it null
            }
        )
    }
}
