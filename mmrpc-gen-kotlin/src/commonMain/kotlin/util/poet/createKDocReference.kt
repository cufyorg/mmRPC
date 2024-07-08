package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker3
fun GenGroup.createKDocReference(element: ElementDefinition): CodeBlock {
    if (element.isAnonymous)
        return CodeBlock.of("`${element.canonicalName.value}`")

    return when (element) {
        is ArrayDefinition,
        is MetadataParameterDefinition,
        is InterDefinition,
        is OptionalDefinition,
        is TupleDefinition,
        -> CodeBlock.of("`${element.canonicalName.value}`")

        is UnionDefinition,
        is StructDefinition,
        is ScalarDefinition,
        is RoutineDefinition,
        is ProtocolDefinition,
        is MetadataDefinition,
        is FieldDefinition,
        is FaultDefinition,
        is ConstDefinition,
        is HttpEndpointDefinition,
        is IframeEndpointDefinition,
        is KafkaEndpointDefinition,
        is KafkaPublicationEndpointDefinition,
        -> CodeBlock.of("[%L]", refOfInfo(element))
    }
}
