package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker0
fun GenGroup.classOfInfo(element: ElementDefinition): ClassName {
    return when (element) {
        is FaultDefinition -> FaultInfo::class.asClassName()
        is FieldDefinition -> FieldInfo::class.asClassName()
        is MetadataDefinition -> MetadataInfo::class.asClassName()
        is MetadataParameterDefinition -> MetadataParameterInfo::class.asClassName()
        is ProtocolDefinition -> ProtocolInfo::class.asClassName()
        is RoutineDefinition -> RoutineInfo::class.asClassName()

        is ArrayDefinition -> ArrayInfo::class.asClassName()
        is ConstDefinition -> ConstInfo::class.asClassName()
        is InterDefinition -> InterInfo::class.asClassName()
        is OptionalDefinition -> OptionalInfo::class.asClassName()
        is ScalarDefinition -> ScalarInfo::class.asClassName()
        is StructDefinition -> StructInfo::class.asClassName()
        is TupleDefinition -> TupleInfo::class.asClassName()
        is UnionDefinition -> UnionInfo::class.asClassName()

        is HttpEndpointDefinition -> HttpEndpointInfo::class.asClassName()
        is IframeEndpointDefinition -> IframeEndpointInfo::class.asClassName()
        is KafkaEndpointDefinition -> KafkaEndpointInfo::class.asClassName()
        is KafkaPublicationEndpointDefinition -> KafkaPublicationEndpointInfo::class.asClassName()
    }
}
