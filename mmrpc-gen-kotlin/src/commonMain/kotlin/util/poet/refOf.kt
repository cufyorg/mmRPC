package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup
import org.cufy.mmrpc.gen.kotlin.util.*

private const val TAG = "refOf"

@Marker3
fun GenGroup.refOfValue(element: ConstDefinition): CodeBlock {
    debugRejectAnonymous(TAG, element)
    debugRejectNative(TAG, element)

    return CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticValue)
}

@Marker3
fun GenGroup.refOfName(element: FieldDefinition): CodeBlock {
    debugRejectAnonymous(TAG, element)

    return CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticName)
}

@Marker3
fun GenGroup.refOfInfo(element: ElementDefinition): CodeBlock {
    debugRejectAnonymous(TAG, element)

    return when (element) {
        is ArrayDefinition,
        is MetadataParameterDefinition,
        is InterDefinition,
        is OptionalDefinition,
        is TupleDefinition,
        -> failGen(TAG, element) { "element unsupported" }

        is UnionDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is StructDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is ScalarDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is RoutineDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is ProtocolDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is MetadataDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is FieldDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is FaultDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is ConstDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is HttpEndpointDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is IframeEndpointDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is KafkaEndpointDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)

        is KafkaPublicationEndpointDefinition
        -> CodeBlock.of("%T.%L.%L", classOf(element.namespace), element.asClassName, element.fStaticInfo)
    }
}

@Marker3
fun GenGroup.refOfInfoOrCreateInfo(element: ElementDefinition): CodeBlock {
    if (element.isAnonymous)
        return createInfo(element)

    return when (element) {
        is MetadataParameterDefinition,

        is ArrayDefinition,
        is InterDefinition,
        is OptionalDefinition,
        is TupleDefinition,

        -> createInfo(element)

        is FaultDefinition,
        is FieldDefinition,
        is MetadataDefinition,
        is ProtocolDefinition,
        is RoutineDefinition,

        is HttpEndpointDefinition,
        is IframeEndpointDefinition,
        is KafkaEndpointDefinition,
        is KafkaPublicationEndpointDefinition,

        is ConstDefinition,
        is ScalarDefinition,
        is StructDefinition,
        is UnionDefinition,

        -> refOfInfo(element)
    }
}
