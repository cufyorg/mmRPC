package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
data class CompactFieldDefinitionUsage(
    val definition_ref: CanonicalName,
    val value: Literal,
)

fun FieldDefinitionUsage.toCompact(strip: Boolean = false): CompactFieldDefinitionUsage {
    return CompactFieldDefinitionUsage(
        definition_ref = this.definition.canonicalName,
        value = this.value,
    )
}

fun CompactFieldDefinitionUsage.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> FieldDefinitionUsage? {
    return it@{
        FieldDefinitionUsage(
            definition = this.definition_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is FieldDefinition) {
                    "<field-usage>.definition_ref must point to a FieldDefinition"
                }
                item
            },
            value = this.value
        )
    }
}
