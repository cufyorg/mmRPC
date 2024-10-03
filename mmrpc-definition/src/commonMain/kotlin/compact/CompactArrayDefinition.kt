package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.ArrayDefinition
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.TypeDefinition

@Serializable
@SerialName("array")
data class CompactArrayDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("array_type.ref")
    val arrayType: CanonicalName,
) : CompactElementDefinition

fun ArrayDefinition.toCompact(): CompactArrayDefinition {
    return CompactArrayDefinition(
        canonicalName = canonicalName,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        arrayType = this.arrayType.canonicalName,
    )
}

fun CompactArrayDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ArrayDefinition? {
    return it@{
        ArrayDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            arrayType = this.arrayType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "array_type.ref must point to a TypeDefinition"
                }
                item
            }
        )
    }
}
