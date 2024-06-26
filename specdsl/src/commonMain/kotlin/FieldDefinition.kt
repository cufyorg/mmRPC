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
@SerialName("field")
data class FieldDefinition(
    override val name: String = "(anonymous<field>)",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("field_type")
    val fieldType: TypeDefinition,
    @SerialName("field_is_optional")
    val fieldIsOptional: Boolean = false,
    @SerialName("field_default")
    val fieldDefault: ConstDefinition? = null,
) : ElementDefinition {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(fieldType.collect())
        fieldDefault?.let { yieldAll(it.collect()) }
    }
}

open class FieldDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = "(anonymous<field>)"

    open var isOptional = false

    open val type = DomainProperty<TypeDefinition>()
    open val default = OptionalDomainProperty<ConstDefinition>()

    override fun build(): FieldDefinition {
        val asNamespace = this.namespace.value + this.name
        return FieldDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            fieldIsOptional = this.isOptional,
            fieldType = this.type.value.get(asNamespace),
            fieldDefault = this.default.value?.get(asNamespace),
        )
    }
}

@Marker1
internal fun prop(
    block: FieldDefinitionBuilder.() -> Unit = {},
): Unnamed<FieldDefinition> {
    return Unnamed { namespace, name ->
        FieldDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
fun prop(
    type: TypeDefinition,
    block: FieldDefinitionBuilder.() -> Unit = {},
): Unnamed<FieldDefinition> {
    return prop { this.type *= type; block() }
}

@Marker1
fun prop(
    type: Unnamed<TypeDefinition>,
    block: FieldDefinitionBuilder.() -> Unit = {},
): Unnamed<FieldDefinition> {
    return prop { this.type *= type; block() }
}

////////////////////////////////////////
