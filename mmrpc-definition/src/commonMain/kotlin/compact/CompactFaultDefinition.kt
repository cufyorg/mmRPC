package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.FaultDefinition
import org.cufy.mmrpc.Namespace

@Serializable
@SerialName("fault")
data class CompactFaultDefinition(
    override val name: String = FaultDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
) : CompactElementDefinition

fun FaultDefinition.toCompact(): CompactFaultDefinition {
    return CompactFaultDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
    )
}

fun CompactFaultDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> FaultDefinition? {
    return it@{
        FaultDefinition(
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
