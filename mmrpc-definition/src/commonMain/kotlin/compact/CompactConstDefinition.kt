package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
@SerialName("const")
data class CompactConstDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val type_ref: CanonicalName,
    val value: Literal,
) : CompactElementDefinition

fun ConstDefinition.toCompact(strip: Boolean = false): CompactConstDefinition {
    return CompactConstDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
        type_ref = this.type.canonicalName,
        value = this.value,
    )
}

fun CompactConstDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ConstDefinition? {
    return it@{
        ConstDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            type = this.type_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "<const>.type_ref must point to a TypeDefinition"
                }
                item
            },
            value = this.value,
        )
    }
}
