package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("enum")
data class CompactEnumDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("enum_type.ref")
    val enumType: CanonicalName,
    @SerialName("enum_entries.ref")
    val enumEntries: List<CanonicalName>,
) : CompactElementDefinition

fun EnumDefinition.toCompact(): CompactEnumDefinition {
    return CompactEnumDefinition(
        canonicalName = canonicalName,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        enumType = this.enumType.canonicalName,
        enumEntries = this.enumEntries
            .map { it.canonicalName }
    )
}

fun CompactEnumDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> EnumDefinition? {
    return it@{
        EnumDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            enumType = this.enumType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "enum_type.ref must point to a TypeDefinition"
                }
                item
            },
            enumEntries = this.enumEntries.map {
                val item = onLookup(it) ?: return@it null
                require(item is ConstDefinition) {
                    "enum_entries.ref must point to a ConstDefinition"
                }
                item
            },
        )
    }
}
