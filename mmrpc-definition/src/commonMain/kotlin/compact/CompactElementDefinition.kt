package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
sealed interface CompactElementDefinition {
    @SerialName("canonical_name")
    val canonicalName: CanonicalName

    val description: String
    val metadata: List<CompactMetadataDefinitionUsage>

    val name get() = canonicalName.name
    val namespace get() = canonicalName.namespace

    val isAnonymous get() = namespace.isAnonymous || Namespace.isAnonymousSegment(name)
}

fun ElementDefinition.toCompact(strip: Boolean = false): CompactElementDefinition {
    return when (this) {
        is ArrayDefinition -> toCompact(strip)
        is ConstDefinition -> toCompact(strip)
        is EnumDefinition -> toCompact(strip)
        is FaultDefinition -> toCompact(strip)
        is FieldDefinition -> toCompact(strip)
        is HttpEndpointDefinition -> toCompact(strip)
        is IframeEndpointDefinition -> toCompact(strip)
        is InterDefinition -> toCompact(strip)
        is KafkaEndpointDefinition -> toCompact(strip)
        is KafkaPublicationEndpointDefinition -> toCompact(strip)
        is MetadataDefinition -> toCompact(strip)
        is OptionalDefinition -> toCompact(strip)
        is ProtocolDefinition -> toCompact(strip)
        is RoutineDefinition -> toCompact(strip)
        is ScalarDefinition -> toCompact(strip)
        is StructDefinition -> toCompact(strip)
        is TupleDefinition -> toCompact(strip)
        is UnionDefinition -> toCompact(strip)
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
        is CompactHttpEndpointDefinition -> inflate(onLookup)
        is CompactIframeEndpointDefinition -> inflate(onLookup)
        is CompactInterDefinition -> inflate(onLookup)
        is CompactKafkaEndpointDefinition -> inflate(onLookup)
        is CompactKafkaPublicationEndpointDefinition -> inflate(onLookup)
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
