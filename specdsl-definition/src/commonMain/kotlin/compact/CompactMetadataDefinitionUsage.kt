package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.MetadataDefinitionUsage
import org.cufy.specdsl.MetadataDefinition

@Serializable
data class CompactMetadataDefinitionUsage(
    @SerialName("definition.ref")
    val definition: CanonicalName,
    val parameters: List<CompactMetadataParameterDefinitionUsage>,
)

fun MetadataDefinitionUsage.toCompact(): CompactMetadataDefinitionUsage {
    return CompactMetadataDefinitionUsage(
        definition = this.definition.canonicalName,
        parameters = this.parameters
            .map { it.toCompact() }
    )
}

fun CompactMetadataDefinitionUsage.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> MetadataDefinitionUsage? {
    return it@{
        MetadataDefinitionUsage(
            definition = this.definition.let {
                val item = onLookup(it) ?: return@it null
                require(item is MetadataDefinition) {
                    "definition.ref must point to a  MetadataDefinition"
                }
                item
            },
            parameters = this.parameters.map {
                it.inflate(onLookup)() ?: return@it null
            }
        )
    }
}
