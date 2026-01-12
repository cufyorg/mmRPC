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
    override val metadata: List<CompactMetadataUsage> = emptyList(),

    val routines_ref: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun ProtocolDefinition.toCompact(): CompactProtocolDefinition {
    return CompactProtocolDefinition(
        canonical_name = this.canonicalName,
        description = this.description,
        metadata = this.metadata.map { it.toCompact() },
        routines_ref = this.routines.map { it.canonicalName },
    )
}

fun CompactProtocolDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): ProtocolDefinition? {
    return ProtocolDefinition(
        canonicalName = this.canonical_name,
        description = this.description,
        metadata = this.metadata.map {
            it.inflateOrNull(onLookup) ?: return null
        },
        routines = this.routines_ref.map {
            val item = onLookup(it) ?: return null
            require(item is RoutineDefinition) {
                "<protocol>.routines_ref must point to a RoutineDefinition"
            }
            item
        },
    )
}
