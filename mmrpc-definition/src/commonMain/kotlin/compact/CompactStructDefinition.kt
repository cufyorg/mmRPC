package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.StructDefinition

@Serializable
@SerialName("struct")
data class CompactStructDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("struct_fields.ref")
    val structFields: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun StructDefinition.toCompact(strip: Boolean = false): CompactStructDefinition {
    return CompactStructDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        structFields = this.structFields
            .map { it.canonicalName },
    )
}

fun CompactStructDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> StructDefinition? {
    return it@{
        StructDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            structFields = this.structFields.map {
                val item = onLookup(it) ?: return@it null
                require(item is FieldDefinition) {
                    "struct_fields.ref must point to a FieldDefinition"
                }
                item
            },
        )
    }
}
