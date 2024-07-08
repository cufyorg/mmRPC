package org.cufy.mmrpc.gen.kotlin.util

import org.cufy.mmrpc.*

fun signatureOf(element: ElementDefinition): String {
    val discriminator = when (element) {
        is ArrayDefinition -> "array"
        is ConstDefinition -> "const"
        is FaultDefinition -> "fault"
        is FieldDefinition -> "field"
        is HttpEndpointDefinition -> "http_endpoint"
        is IframeEndpointDefinition -> "iframe_endpoint"
        is InterDefinition -> "inter"
        is KafkaEndpointDefinition -> "kafka_endpoint"
        is KafkaPublicationEndpointDefinition -> "kafka_publication_endpoint"
        is MetadataDefinition -> "metadata"
        is MetadataParameterDefinition -> "metadata-parameter"
        is OptionalDefinition -> "optional"
        is ProtocolDefinition -> "protocol"
        is RoutineDefinition -> "routine"
        is ScalarDefinition -> "scalar"
        is StructDefinition -> "struct"
        is TupleDefinition -> "tuple"
        is UnionDefinition -> "union"
    }

    return "$discriminator ${element.canonicalName.value}"
}
