package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("optional")
data class CompactOptionalDefinition(
    override val name: String = OptionalDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("optional_type.ref")
    val optionalType: CanonicalName,
) : CompactElementDefinition

fun OptionalDefinition.toCompact(): CompactOptionalDefinition {
    return CompactOptionalDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        optionalType = this.optionalType.canonicalName,
    )
}

fun CompactOptionalDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> OptionalDefinition? {
    return it@{
        OptionalDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            optionalType = this.optionalType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "optional_type.ref must point to a TypeDefinition"
                }
                item
            }
        )
    }
}
