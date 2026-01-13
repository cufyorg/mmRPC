package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FieldDefinition
import org.cufy.mmrpc.TraitDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("trait")
data class CompactTraitDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataUsage> = emptyList(),

    val discriminator: String,
    val traits_ref: List<CanonicalName> = emptyList(),
    val fields_ref: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun TraitDefinition.toCompact(): CompactTraitDefinition {
    return CompactTraitDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        discriminator = this.discriminator,
        traits_ref = this.traits.map { it.canonicalName },
        fields_ref = this.fields.map { it.canonicalName },
    )
}

fun CompactTraitDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): TraitDefinition? {
    return TraitDefinition(
        canonicalName = this.canonical_name,
        description = this.description,
        metadata = this.metadata.map {
            it.inflateOrNull(onLookup) ?: return null
        },
        discriminator = this.discriminator,
        traits = this.traits_ref.map {
            val item = onLookup(it) ?: return null
            require(item is TraitDefinition) {
                "<trait>.traits_ref must point to a TraitDefinition"
            }
            item
        },
        fields = this.fields_ref.map {
            val item = onLookup(it) ?: return null
            require(item is FieldDefinition) {
                "<trait>.fields_ref must point to a FieldDefinition"
            }
            item
        },
    )
}
