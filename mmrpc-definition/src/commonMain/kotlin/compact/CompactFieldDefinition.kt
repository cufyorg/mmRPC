package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
@SerialName("field")
data class CompactFieldDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val type_ref: CanonicalName,
    val default: Literal? = null,
) : CompactElementDefinition

fun FieldDefinition.toCompact(): CompactFieldDefinition {
    return CompactFieldDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        type_ref = this.type.canonicalName,
        default = this.default,
    )
}

fun CompactFieldDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> FieldDefinition? {
    return it@{
        FieldDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            type = this.type_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "<field>.type_ref must point to a TypeDefinition"
                }
                item
            },
            default = this.default
        )
    }
}
