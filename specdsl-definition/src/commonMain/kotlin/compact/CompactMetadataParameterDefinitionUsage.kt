package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
data class CompactMetadataParameterDefinitionUsage(
    @SerialName("definition.ref")
    val definition: CanonicalName,
    val value: Literal,
)

fun MetadataParameterDefinitionUsage.toCompact(): CompactMetadataParameterDefinitionUsage {
    return CompactMetadataParameterDefinitionUsage(
        definition = this.definition.canonicalName,
        value = this.value,
    )
}

fun CompactMetadataParameterDefinitionUsage.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> MetadataParameterDefinitionUsage? {
    return it@{
        MetadataParameterDefinitionUsage(
            definition = this.definition.let {
                val item = onLookup(it) ?: return@it null
                require(item is MetadataParameterDefinition) {
                    "definition.ref must point to a MetadataParameterDefinition"
                }
                item
            },
            value = this.value
        )
    }
}
