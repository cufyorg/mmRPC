package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("struct")
data class CompactStructDefinition(
    override val name: String = StructDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("struct_fields.ref")
    val structFields: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun StructDefinition.toCompact(): CompactStructDefinition {
    return CompactStructDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        structFields = this.structFields
            .map { it.canonicalName },
    )
}

fun CompactStructDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> StructDefinition? {
    return it@{
        StructDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
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
