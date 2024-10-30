package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ScalarDefinition

@Serializable
@SerialName("scalar")
data class CompactScalarDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("scalar_type.ref")
    val scalarType: CanonicalName? = null,
) : CompactElementDefinition

fun ScalarDefinition.toCompact(strip: Boolean = false): CompactScalarDefinition {
    return CompactScalarDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        scalarType = this.scalarType?.canonicalName,
    )
}

fun CompactScalarDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ScalarDefinition? {
    return it@{
        ScalarDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            scalarType = this.scalarType?.let {
                val item = onLookup(it) ?: return@it null
                require(item is ScalarDefinition) {
                    "scalar_type.ref must point to a ScalarDefinition"
                }
                item
            }
        )
    }
}
