package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.Metadata
import org.cufy.specdsl.MetadataDefinition

@Serializable
data class CompactMetadata(
    @SerialName("definition.ref")
    val definition: CanonicalName,
    val parameters: List<CompactMetadataParameter>,
)

fun Metadata.toCompact(): CompactMetadata {
    return CompactMetadata(
        definition = this.definition.canonicalName,
        parameters = this.parameters
            .map { it.toCompact() }
    )
}

fun CompactMetadata.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> Metadata? {
    return it@{
        Metadata(
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
