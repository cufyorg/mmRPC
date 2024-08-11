package org.cufy.mmrpc.gen.kotlin.util.gen.structures

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

/**
 * Return code that, when executed, returns [ElementInfo] representing the given [element].
 */
@Marker3
fun GenGroup.createInfo(element: ElementDefinition): CodeBlock {
    return when (element) {
        is ConstDefinition -> createInfo(element)
        is FaultDefinition -> createInfo(element)
        is FieldDefinition -> createInfo(element)
        is MetadataDefinition -> createInfo(element)
        is ProtocolDefinition -> createInfo(element)
        is RoutineDefinition -> createInfo(element)

        is ArrayDefinition -> createInfo(element)
        is EnumDefinition -> createInfo(element)
        is InterDefinition -> createInfo(element)
        is OptionalDefinition -> createInfo(element)
        is ScalarDefinition -> createInfo(element)
        is StructDefinition -> createInfo(element)
        is TupleDefinition -> createInfo(element)
        is UnionDefinition -> createInfo(element)

        is HttpEndpointDefinition -> createInfo(element)
        is IframeEndpointDefinition -> createInfo(element)
        is KafkaEndpointDefinition -> createInfo(element)
        is KafkaPublicationEndpointDefinition -> createInfo(element)
    }
}
