package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.StructDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("struct")
data class CompactStructDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val fields_ref: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun StructDefinition.toCompact(): CompactStructDefinition {
    return CompactStructDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        fields_ref = this.fields.map { it.canonicalName },
    )
}

fun CompactStructDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): StructDefinition? {
    return StructDefinition(
        canonicalName = this.canonical_name,
        description = this.description,
        metadata = this.metadata.map {
            it.inflateOrNull(onLookup)() ?: return null
        },
        fields = this.fields_ref.map {
            val item = onLookup(it) ?: return null
            require(item is FieldDefinition) {
                "<struct>.fields_ref must point to a FieldDefinition"
            }
            item
        },
    )
}
