package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("field")
data class CompactFieldDefinition(
    override val name: String = "(anonymous<field>)",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("field_type.ref")
    val fieldType: CanonicalName,
    @SerialName("field_is_optional")
    val fieldIsOptional: Boolean = false,
    @SerialName("field_default.ref")
    val fieldDefault: CanonicalName? = null,
) : CompactElementDefinition

fun FieldDefinition.toCompact(): CompactFieldDefinition {
    return CompactFieldDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        fieldType = this.fieldType.canonicalName,
        fieldIsOptional = this.fieldIsOptional,
        fieldDefault = this.fieldDefault?.canonicalName,
    )
}

fun CompactFieldDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> FieldDefinition? {
    return it@{
        FieldDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
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
            fieldIsOptional = this.fieldIsOptional,
            fieldDefault = this.fieldDefault?.let {
                val item = onLookup(it) ?: return@it null
                require(item is ConstDefinition) {
                    "field_default.ref must point to a ConstDefinition"
                }
                item
            }
        )
    }
}
