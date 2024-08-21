/*
 *	Copyright 2024 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.cufy.mmrpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//

@Serializable
sealed class ElementInfo {
    abstract val name: String
    abstract val namespace: Namespace
    abstract val metadata: List<MetadataInfoUsage>

    val canonicalName get() = CanonicalName(namespace, name)
    val isAnonymous get() = namespace.isAnonymous || Namespace.isAnonymousSegment(name)
    val asNamespace get() = namespace + name
}

@Serializable
sealed class TypeInfo : ElementInfo()

@Serializable
sealed class EndpointInfo : ElementInfo()

// usage

@Serializable
data class MetadataInfoUsage(
    val info: MetadataInfo,
    val fields: List<FieldInfoUsage>,
)

@Serializable
data class FieldInfoUsage(
    val info: FieldInfo,
    val value: Literal,
)

// elements

@Serializable
@SerialName("const")
data class ConstInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("const_type")
    val type: TypeInfo,
    @SerialName("const_value")
    val value: Literal,
) : ElementInfo()

@Serializable
@SerialName("fault")
data class FaultInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
) : ElementInfo()

@Serializable
@SerialName("field")
data class FieldInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("field_type")
    val type: TypeInfo,
    @SerialName("field_default")
    val default: Literal?,
) : ElementInfo()

@Serializable
@SerialName("metadata")
data class MetadataInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("metadata_fields")
    val fields: List<FieldInfo> = emptyList(),
) : ElementInfo()

@Serializable
@SerialName("protocol")
data class ProtocolInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("protocol_routines")
    val routines: List<RoutineInfo>,
) : ElementInfo()

@Serializable
@SerialName("routine")
data class RoutineInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("routine_endpoints")
    val endpoints: List<EndpointInfo>,
    @SerialName("routine_fault_union")
    val fault: List<FaultInfo>,
    @SerialName("routine_input")
    val input: StructInfo,
    @SerialName("routine_output")
    val output: StructInfo,
    @SerialName("routine_key")
    val key: TupleInfo?,
) : ElementInfo()

// endpoints

@Serializable
@SerialName("http_endpoint")
data class HttpEndpointInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("endpoint_path")
    val path: HttpPath,
    @SerialName("endpoint_method_union")
    val method: List<HttpMethod>,
    @SerialName("endpoint_security_inter")
    val security: List<HttpSecurity>,
) : EndpointInfo()

@Serializable
@SerialName("iframe_endpoint")
data class IframeEndpointInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("endpoint_path")
    val path: IframePath,
    @SerialName("endpoint_security_inter")
    val security: List<IframeSecurity>,
) : EndpointInfo()

@Serializable
@SerialName("kafka_endpoint")
data class KafkaEndpointInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("endpoint_topic")
    val topic: KafkaTopic,
    @SerialName("endpoint_security_inter")
    val security: List<KafkaSecurity>,
) : EndpointInfo()

@Serializable
@SerialName("kafka_publication_endpoint")
data class KafkaPublicationEndpointInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("endpoint_topic")
    val topic: KafkaPublicationTopic,
    @SerialName("endpoint_security_inter")
    val security: List<KafkaPublicationSecurity>,
) : EndpointInfo()

// types

@Serializable
@SerialName("array")
data class ArrayInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("array_type")
    val type: TypeInfo,
) : TypeInfo()

@Serializable
@SerialName("enum")
data class EnumInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("enum_type")
    val type: TypeInfo,
    @SerialName("enum_entries")
    val entries: List<ConstInfo>,
) : TypeInfo()

@Serializable
@SerialName("inter")
data class InterInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("inter_types")
    val types: List<StructInfo>,
) : TypeInfo()

@Serializable
@SerialName("optional")
data class OptionalInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("optional_type")
    val type: TypeInfo,
) : TypeInfo()

@Serializable
@SerialName("scalar")
data class ScalarInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
) : TypeInfo()

@Serializable
@SerialName("struct")
data class StructInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("struct_fields")
    val fields: List<FieldInfo> = emptyList(),
) : TypeInfo()

@Serializable
@SerialName("tuple")
data class TupleInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("tuple_types")
    val types: List<TypeInfo>,
) : TypeInfo()

@Serializable
@SerialName("union")
data class UnionInfo(
    override val name: String,
    override val namespace: Namespace,
    override val metadata: List<MetadataInfoUsage>,
    @SerialName("union_discriminator")
    val discriminator: String,
    @SerialName("union_types")
    val types: List<StructInfo>,
) : TypeInfo()
