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
@SerialName("optional")
data class OptionalDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),

    val type: TypeDefinition,
) : TypeDefinition()

class OptionalDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = DomainProperty<TypeDefinition>()

    override fun build(): OptionalDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return OptionalDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this.type.value.get(canonicalName, name = "type"),
        )
    }
}

@Marker2
internal fun optional(
    block: OptionalDefinitionBuilder.() -> Unit = {},
): Unnamed<OptionalDefinition> {
    return Unnamed { namespace, name ->
        OptionalDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
fun optional(
    type: TypeDefinition,
    block: OptionalDefinitionBuilder.() -> Unit = {},
) = optional { this.type *= type; block() }

@Marker2
fun optional(
    type: Unnamed<TypeDefinition>,
    block: OptionalDefinitionBuilder.() -> Unit = {},
) = optional { this.type *= type; block() }

////////////////////////////////////////

@Marker2
val TypeDefinition.optional: Unnamed<OptionalDefinition>
    get() = optional(this)

@Marker2
val Unnamed<TypeDefinition>.optional: Unnamed<OptionalDefinition>
    get() = optional(this)

////////////////////////////////////////
