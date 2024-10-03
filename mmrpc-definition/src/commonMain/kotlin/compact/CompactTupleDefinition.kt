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
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("tuple_types.ref")
    val tupleTypes: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun TupleDefinition.toCompact(): CompactTupleDefinition {
    return CompactTupleDefinition(
        canonicalName = canonicalName,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
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
            isInline = this.isInline,
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
