package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("array")
data class CompactArrayDefinition(
    override val name: String = "(anonymous[])",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("array_type.ref")
    val arrayType: CanonicalName,
) : CompactElementDefinition

fun ArrayDefinition.toCompact(): CompactArrayDefinition {
    return CompactArrayDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        arrayType = this.arrayType.canonicalName,
    )
}

fun CompactArrayDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
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
