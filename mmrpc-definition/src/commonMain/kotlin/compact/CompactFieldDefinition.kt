package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("field")
data class CompactFieldDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("field_type.ref")
    val fieldType: CanonicalName,
    @SerialName("field_default")
    val fieldDefault: Literal? = null,
) : CompactElementDefinition

fun FieldDefinition.toCompact(): CompactFieldDefinition {
    return CompactFieldDefinition(
        canonicalName = canonicalName,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        fieldType = this.fieldType.canonicalName,
        fieldDefault = this.fieldDefault,
    )
}

fun CompactFieldDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> FieldDefinition? {
    return it@{
        FieldDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            fieldType = this.fieldType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "field_type.ref must point to a TypeDefinition"
                }
                item
            },
            fieldDefault = this.fieldDefault
        )
    }
}
