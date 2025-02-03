package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.RoutineDefinition

@Suppress("PropertyName")
@Serializable
@SerialName("protocol")
data class CompactProtocolDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val routines_ref: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun ProtocolDefinition.toCompact(strip: Boolean = false): CompactProtocolDefinition {
    return CompactProtocolDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
        routines_ref = this.routines.map { it.canonicalName },
    )
}

fun CompactProtocolDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ProtocolDefinition? {
    return it@{
        ProtocolDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            routines = this.routines_ref.map {
                val item = onLookup(it) ?: return@it null
                require(item is RoutineDefinition) {
                    "<protocol>.routines_ref must point to a RoutineDefinition"
                }
                item
            },
        )
    }
}
