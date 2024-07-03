package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
fun GenGroup.createInfo(element: ElementDefinition): CodeBlock {
    return when (element) {
        is FaultDefinition -> createInfo(element)
        is FieldDefinition -> createInfo(element)
        is MetadataDefinition -> createInfo(element)
        is MetadataParameterDefinition -> createInfo(element)
        is ProtocolDefinition -> createInfo(element)
        is RoutineDefinition -> createInfo(element)

        is ArrayDefinition -> createInfo(element)
        is ConstDefinition -> createInfo(element)
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
