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

fun CompactElementDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ElementDefinition? {
    return when (this) {
        is CompactArrayDefinition -> inflate(onLookup)
        is CompactConstDefinition -> inflate(onLookup)
        is CompactEnumDefinition -> inflate(onLookup)
        is CompactFaultDefinition -> inflate(onLookup)
        is CompactFieldDefinition -> inflate(onLookup)
        is CompactInterDefinition -> inflate(onLookup)
        is CompactMetadataDefinition -> inflate(onLookup)
        is CompactOptionalDefinition -> inflate(onLookup)
        is CompactProtocolDefinition -> inflate(onLookup)
        is CompactRoutineDefinition -> inflate(onLookup)
        is CompactScalarDefinition -> inflate(onLookup)
        is CompactStructDefinition -> inflate(onLookup)
        is CompactTupleDefinition -> inflate(onLookup)
        is CompactUnionDefinition -> inflate(onLookup)
    }
}
