package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("inter")
data class CompactInterDefinition(
    override val name: String = InterDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("inter_types.ref")
    val interTypes: List<CanonicalName>,
) : CompactElementDefinition

fun InterDefinition.toCompact(): CompactInterDefinition {
    return CompactInterDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        interTypes = this.interTypes
            .map { it.canonicalName },
    )
}

fun CompactInterDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> InterDefinition? {
    return it@{
        InterDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            interTypes = this.interTypes.map {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "inter_types.ref must point to a TypeDefinition"
                }
                item
            },
        )
    }
}
