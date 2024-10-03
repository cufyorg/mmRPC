package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.TupleDefinition
import org.cufy.mmrpc.TypeDefinition

@Serializable
@SerialName("tuple")
data class CompactTupleDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("tuple_types.ref")
    val tupleTypes: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun TupleDefinition.toCompact(strip: Boolean = false): CompactTupleDefinition {
    return CompactTupleDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        tupleTypes = this.tupleTypes
            .map { it.canonicalName },
    )
}

fun CompactTupleDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> TupleDefinition? {
    return it@{
        TupleDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            tupleTypes = this.tupleTypes.map {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "tuple_types.ref must point to a TypeDefinition"
                }
                item
            },
        )
    }
}
