package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ScalarDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("scalar")
data class CompactScalarDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val type_ref: CanonicalName? = null,
) : CompactElementDefinition

fun ScalarDefinition.toCompact(): CompactScalarDefinition {
    return CompactScalarDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        type_ref = this.type?.canonicalName,
    )
}

fun CompactScalarDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): ScalarDefinition? {
    return ScalarDefinition(
        canonicalName = this.canonical_name,
        description = this.description,
        metadata = this.metadata.map {
            it.inflateOrNull(onLookup)() ?: return null
        },
        type = this.type_ref?.let {
            val item = onLookup(it) ?: return null
            require(item is ScalarDefinition) {
                "<scalar>.type_ref must point to a ScalarDefinition"
            }
            item
        }
    )
}
