package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.TypeDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("tuple")
data class CompactTupleDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val types_ref: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun TupleDefinition.toCompact(): CompactTupleDefinition {
    return CompactTupleDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        types_ref = this.types.map { it.canonicalName },
    )
}

fun CompactTupleDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> TupleDefinition? {
    return it@{
        TupleDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            types = this.types_ref.map {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "<tuple>.types_ref must point to a TypeDefinition"
                }
                item
            },
        )
    }
}
