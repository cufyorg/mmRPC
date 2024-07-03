package org.cufy.specdsl.gen.kotlin.util

import org.cufy.specdsl.*

//

inline val FaultDefinition.fStaticInfo get() = "INFO"
inline val FieldDefinition.fStaticInfo get() = "INFO"
inline val MetadataDefinition.fStaticInfo get() = "INFO"
inline val ProtocolDefinition.fStaticInfo get() = "INFO"
inline val RoutineDefinition.fStaticInfo get() = "INFO"

//

inline val HttpEndpointDefinition.fStaticInfo get() = "INFO"
inline val IframeEndpointDefinition.fStaticInfo get() = "INFO"
inline val KafkaEndpointDefinition.fStaticInfo get() = "INFO"
inline val KafkaPublicationEndpointDefinition.fStaticInfo get() = "INFO"

//

inline val ConstDefinition.fStaticInfo get() = "INFO"
inline val ScalarDefinition.fStaticInfo get() = "INFO"
inline val StructDefinition.fStaticInfo get() = "INFO"
inline val UnionDefinition.fStaticInfo get() = "INFO"

//

inline val ConstDefinition.fStaticValue get() = "VALUE"
inline val FieldDefinition.fStaticName get() = "NAME"

inline val HttpEndpointDefinition.fStaticPath get() = "PATH"
inline val IframeEndpointDefinition.fStaticPath get() = "PATH"
inline val KafkaEndpointDefinition.fStaticTopic get() = "TOPIC"
inline val KafkaPublicationEndpointDefinition.fStaticTopic get() = "TOPIC"

// asClassName

val Namespace.asClassName: String
    get() = segments.joinToString("_") {
        it.replace(':', '_')
    }

//

inline val FaultDefinition.asClassName get() = name
inline val FieldDefinition.asClassName get() = name
inline val MetadataDefinition.asClassName get() = name
inline val ProtocolDefinition.asClassName get() = name
inline val RoutineDefinition.asClassName get() = name.replace(":", "_")

//

inline val HttpEndpointDefinition.asClassName get() = name
inline val IframeEndpointDefinition.asClassName get() = name
inline val KafkaEndpointDefinition.asClassName get() = name
inline val KafkaPublicationEndpointDefinition.asClassName get() = name

//

inline val ConstDefinition.asClassName get() = name
inline val ScalarDefinition.asClassName get() = name
inline val StructDefinition.asClassName get() = name
inline val UnionDefinition.asClassName get() = name

//
