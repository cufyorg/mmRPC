package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
data class CompactFieldDefinitionUsage(
    @SerialName("definition.ref")
    val definition: CanonicalName,
    val value: Literal,
)

fun FieldDefinitionUsage.toCompact(strip: Boolean = false): CompactFieldDefinitionUsage {
    return CompactFieldDefinitionUsage(
        definition = this.definition.canonicalName,
        value = this.value,
    )
}

fun CompactFieldDefinitionUsage.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> FieldDefinitionUsage? {
    return it@{
        FieldDefinitionUsage(
            definition = this.definition.let {
                val item = onLookup(it) ?: return@it null
                require(item is FieldDefinition) {
                    "definition.ref must point to a FieldDefinition"
                }
                item
            },
            value = this.value
        )
    }
}
