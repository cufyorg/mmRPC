package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.CanonicalName
import org.cufy.specdsl.ElementDefinition
import org.cufy.specdsl.FaultDefinition
import org.cufy.specdsl.Namespace

@Serializable
@SerialName("fault")
data class CompactFaultDefinition(
    override val name: String = FaultDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
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
