package org.cufy.mmrpc.gen.kotlin.util

import org.cufy.mmrpc.*
import kotlin.reflect.KClass

/**
 * The appropriate info class for this element definition.
 */
val ElementDefinition.infoClass: KClass<out ElementInfo>
    get() = when (this) {
        is ConstDefinition -> ConstInfo::class
        is FaultDefinition -> FaultInfo::class
        is FieldDefinition -> FieldInfo::class
        is MetadataDefinition -> MetadataInfo::class
        is ProtocolDefinition -> ProtocolInfo::class
        is RoutineDefinition -> RoutineInfo::class
        is ArrayDefinition -> ArrayInfo::class
        is EnumDefinition -> EnumInfo::class
        is InterDefinition -> InterInfo::class
        is OptionalDefinition -> OptionalInfo::class
        is ScalarDefinition -> ScalarInfo::class
        is StructDefinition -> StructInfo::class
        is TupleDefinition -> TupleInfo::class
        is UnionDefinition -> UnionInfo::class

        is HttpEndpointDefinition -> HttpEndpointInfo::class
        is IframeEndpointDefinition -> IframeEndpointInfo::class
        is KafkaEndpointDefinition -> KafkaEndpointInfo::class
        is KafkaPublicationEndpointDefinition -> KafkaPublicationEndpointInfo::class
    }
