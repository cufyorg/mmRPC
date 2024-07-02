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
package org.cufy.specdsl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

////////////////////////////////////////

@Serializable
@SerialName("optional")
data class OptionalDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("optional_type")
    val optionalType: TypeDefinition,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous?)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(optionalType.collect())
    }
}

open class OptionalDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = OptionalDefinition.ANONYMOUS_NAME

    open val type = DomainProperty<TypeDefinition>()

    override fun build(): OptionalDefinition {
        val asNamespace = this.namespace.value + this.name
        return OptionalDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            optionalType = this.type.value.get(asNamespace, name = "type"),
        )
    }
}

@Marker1
internal fun optional(
    block: OptionalDefinitionBuilder.() -> Unit = {}
): Unnamed<OptionalDefinition> {
    return Unnamed { namespace, name, isInline ->
        OptionalDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = isInline }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
fun optional(
    type: TypeDefinition,
    block: OptionalDefinitionBuilder.() -> Unit = {},
): Unnamed<OptionalDefinition> {
    return optional { this.type *= type; block() }
}

@Marker1
fun optional(
    type: Unnamed<TypeDefinition>,
    block: OptionalDefinitionBuilder.() -> Unit = {},
): Unnamed<OptionalDefinition> {
    return optional { this.type *= type; block() }
}

////////////////////////////////////////

@Marker1
val TypeDefinition.optional: Unnamed<OptionalDefinition>
    get() = optional(this)

@Marker1
val Unnamed<TypeDefinition>.optional: Unnamed<OptionalDefinition>
    get() = optional(this)

////////////////////////////////////////
