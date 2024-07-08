package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.Namespace
import org.cufy.mmrpc.ScalarDefinition

@Serializable
@SerialName("scalar")
data class CompactScalarDefinition(
    override val name: String = ScalarDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
) : CompactElementDefinition

fun ScalarDefinition.toCompact(): CompactScalarDefinition {
    return CompactScalarDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
    )
}

fun CompactScalarDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> ScalarDefinition? {
    return it@{
        ScalarDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
        )
    }
}
