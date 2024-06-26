package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.MetadataParameter
import org.cufy.specdsl.MetadataParameterDefinition

@Serializable
data class CompactMetadataParameter(
    @SerialName("definition.ref")
    val definition: CanonicalName,
    val value: String,
)

fun MetadataParameter.toCompact(): CompactMetadataParameter {
    return CompactMetadataParameter(
        definition = this.definition.canonicalName,
        value = this.value,
    )
}

fun CompactMetadataParameter.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> MetadataParameter? {
    return it@{
        MetadataParameter(
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
