package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("const")
data class CompactConstDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("const_type.ref")
    val constType: CanonicalName,
    @SerialName("const_value")
    val constValue: Literal,
) : CompactElementDefinition

fun ConstDefinition.toCompact(strip: Boolean = false): CompactConstDefinition {
    return CompactConstDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        constType = this.constType.canonicalName,
        constValue = this.constValue,
    )
}

fun CompactConstDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ConstDefinition? {
    return it@{
        ConstDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            constType = this.constType.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "const_type.ref must point to a TypeDefinition"
                }
                item
            },
            constValue = this.constValue,
        )
    }
}
