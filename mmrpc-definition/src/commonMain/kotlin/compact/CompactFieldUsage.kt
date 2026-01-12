package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
data class CompactFieldUsage(
    val definition_ref: CanonicalName,
    val value: Literal,
)

fun FieldUsage.toCompact(): CompactFieldUsage {
    return CompactFieldUsage(
        definition_ref = this.definition.canonicalName,
        value = this.value,
    )
}

fun CompactFieldUsage.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): FieldUsage? {
    return FieldUsage(
        definition = this.definition_ref.let {
            val item = onLookup(it) ?: return null
            require(item is FieldDefinition) {
                "<field-usage>.definition_ref must point to a FieldDefinition"
            }
            item
        },
        value = this.value
    )
}
