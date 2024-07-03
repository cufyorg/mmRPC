package org.cufy.specdsl.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.specdsl.*
import org.cufy.specdsl.gen.kotlin.GenGroup

@Marker0
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
