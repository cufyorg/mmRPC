package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("protocol")
data class CompactProtocolDefinition(
    override val name: String = ProtocolDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("protocol_routines.ref")
    val protocolRoutines: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun ProtocolDefinition.toCompact(): CompactProtocolDefinition {
    return CompactProtocolDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        protocolRoutines = this.protocolRoutines
            .map { it.canonicalName },
    )
}

fun CompactProtocolDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> ProtocolDefinition? {
    return it@{
        ProtocolDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            protocolRoutines = this.protocolRoutines.map {
                val item = onLookup(it) ?: return@it null
                require(item is RoutineDefinition) {
                    "protocol_routines.ref must point to a RoutineDefinition"
                }
                item
            },
        )
    }
}
