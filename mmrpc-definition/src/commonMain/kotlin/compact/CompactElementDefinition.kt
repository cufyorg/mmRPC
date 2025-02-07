package org.cufy.mmrpc.compact

import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
sealed interface CompactElementDefinition {
    val canonical_name: CanonicalName

    val description: String
    val metadata: List<CompactMetadataDefinitionUsage>

    val name get() = canonical_name.name
    val namespace get() = canonical_name.namespace
}

fun ElementDefinition.toCompact(): CompactElementDefinition {
    return when (this) {
        is ArrayDefinition -> toCompact()
        is ConstDefinition -> toCompact()
        is EnumDefinition -> toCompact()
        is FaultDefinition -> toCompact()
        is FieldDefinition -> toCompact()
        is InterDefinition -> toCompact()
        is MetadataDefinition -> toCompact()
        is OptionalDefinition -> toCompact()
        is ProtocolDefinition -> toCompact()
        is RoutineDefinition -> toCompact()
        is ScalarDefinition -> toCompact()
        is StructDefinition -> toCompact()
        is TupleDefinition -> toCompact()
        is UnionDefinition -> toCompact()
    }
}

fun CompactElementDefinition.inflateOrNull(
    onLookup: (CanonicalName) -> ElementDefinition?,
): ElementDefinition? {
    return when (this) {
        is CompactArrayDefinition -> inflateOrNull(onLookup)
        is CompactConstDefinition -> inflateOrNull(onLookup)
        is CompactEnumDefinition -> inflateOrNull(onLookup)
        is CompactFaultDefinition -> inflateOrNull(onLookup)
        is CompactFieldDefinition -> inflateOrNull(onLookup)
        is CompactInterDefinition -> inflateOrNull(onLookup)
        is CompactMetadataDefinition -> inflateOrNull(onLookup)
        is CompactOptionalDefinition -> inflateOrNull(onLookup)
        is CompactProtocolDefinition -> inflateOrNull(onLookup)
        is CompactRoutineDefinition -> inflateOrNull(onLookup)
        is CompactScalarDefinition -> inflateOrNull(onLookup)
        is CompactStructDefinition -> inflateOrNull(onLookup)
        is CompactTupleDefinition -> inflateOrNull(onLookup)
        is CompactUnionDefinition -> inflateOrNull(onLookup)
    }
}
