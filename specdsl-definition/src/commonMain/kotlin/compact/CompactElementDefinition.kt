package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
sealed interface CompactElementDefinition {
    val name: String
    val namespace: Namespace

    @SerialName("is_inline")
    val isInline: Boolean
    val description: String
    val metadata: List<CompactMetadataDefinitionUsage>

    val canonicalName get() = CanonicalName(namespace, name)
    val isAnonymous get() = namespace.isAnonymous || Namespace.isAnonymousSegment(name)
}

fun ElementDefinition.toCompact(): CompactElementDefinition {
    return when (this) {
        is ArrayDefinition -> toCompact()
        is ConstDefinition -> toCompact()
        is FaultDefinition -> toCompact()
        is FieldDefinition -> toCompact()
        is HttpEndpointDefinition -> toCompact()
        is IframeEndpointDefinition -> toCompact()
        is InterDefinition -> toCompact()
        is KafkaEndpointDefinition -> toCompact()
        is KafkaPublicationEndpointDefinition -> toCompact()
        is MetadataDefinition -> toCompact()
        is MetadataParameterDefinition -> toCompact()
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
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> ElementDefinition? {
    return when (this) {
        is CompactArrayDefinition -> inflate(onLookup)
        is CompactConstDefinition -> inflate(onLookup)
        is CompactFaultDefinition -> inflate(onLookup)
        is CompactFieldDefinition -> inflate(onLookup)
        is CompactHttpEndpointDefinition -> inflate(onLookup)
        is CompactIframeEndpointDefinition -> inflate(onLookup)
        is CompactInterDefinition -> inflate(onLookup)
        is CompactKafkaEndpointDefinition -> inflate(onLookup)
        is CompactKafkaPublicationEndpointDefinition -> inflate(onLookup)
        is CompactMetadataDefinition -> inflate(onLookup)
        is CompactMetadataParameterDefinition -> inflate(onLookup)
        is CompactOptionalDefinition -> inflate(onLookup)
        is CompactProtocolDefinition -> inflate(onLookup)
        is CompactRoutineDefinition -> inflate(onLookup)
        is CompactScalarDefinition -> inflate(onLookup)
        is CompactStructDefinition -> inflate(onLookup)
        is CompactTupleDefinition -> inflate(onLookup)
        is CompactUnionDefinition -> inflate(onLookup)
    }
}
