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

////////////////////////////////////////

@Serializable
sealed class ElementDefinition {
    abstract val canonicalName: CanonicalName
    abstract val description: String
    abstract val metadata: List<MetadataUsage>

    val name by lazy { canonicalName.name }
    val namespace by lazy { canonicalName.namespace }
}

@Serializable
@SerialName("const")
data class ConstDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val type: TypeDefinition,
    val value: Literal,
) : ElementDefinition()

@Serializable
@SerialName("field")
data class FieldDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val key: String? = null,
    val type: TypeDefinition,
    val default: Literal? = null,
) : ElementDefinition()

@Serializable
@SerialName("fault")
data class FaultDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),
) : ElementDefinition()

@Serializable
@SerialName("metadata")
data class MetadataDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val fields: List<FieldDefinition> = emptyList(),
) : ElementDefinition()

@Serializable
@SerialName("protocol")
data class ProtocolDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val routines: List<RoutineDefinition> = emptyList(),
) : ElementDefinition()

@Serializable
@SerialName("routine")
data class RoutineDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val comm: List<Comm> = emptyList(),
    val faults: List<FaultDefinition> = emptyList(),
    val input: StructDefinition,
    val output: StructDefinition,
) : ElementDefinition()

////////////////////////////////////////

@Serializable
sealed class TypeDefinition : ElementDefinition()

@Serializable
@SerialName("array")
data class ArrayDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val type: TypeDefinition,
) : TypeDefinition()

@Serializable
@SerialName("map")
data class MapDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val type: TypeDefinition,
) : TypeDefinition()

@Serializable
@SerialName("enum")
data class EnumDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val type: TypeDefinition,
    val entries: List<ConstDefinition>,
) : TypeDefinition()

@Serializable
@SerialName("inter")
data class InterDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val types: List<StructDefinition>,
) : TypeDefinition()

@Serializable
@SerialName("optional")
data class OptionalDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val type: TypeDefinition,
) : TypeDefinition()

@Serializable
@SerialName("scalar")
data class ScalarDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val type: ScalarDefinition? = null,
) : TypeDefinition()

@Serializable
@SerialName("struct")
data class StructDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val fields: List<FieldDefinition> = emptyList(),
) : TypeDefinition()

@Serializable
@SerialName("tuple")
data class TupleDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val types: List<TypeDefinition> = emptyList(),
) : TypeDefinition()

@Serializable
@SerialName("union")
data class UnionDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataUsage> = emptyList(),

    val discriminator: String = "type",
    val types: List<StructDefinition>,
) : TypeDefinition()

////////////////////////////////////////

@Serializable
data class FieldUsage(
    val definition: FieldDefinition,
    val value: Literal,
)

@Serializable
data class MetadataUsage(
    val definition: MetadataDefinition,
    val fields: List<FieldUsage>,
)

////////////////////////////////////////
