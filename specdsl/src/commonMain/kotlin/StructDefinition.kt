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
import kotlin.jvm.JvmName

////////////////////////////////////////

@Serializable
@SerialName("struct")
data class StructDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("struct_fields")
    val structFields: List<FieldDefinition> = emptyList(),
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous{})"
        val Empty = StructDefinition()
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(structFields.asSequence().flatMap { it.collect() })
    }
}

open class StructDefinitionBuilder :
    FieldDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = StructDefinition.ANONYMOUS_NAME

    protected open var structFieldsUnnamed = mutableListOf<Unnamed<FieldDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedFieldDefinition")
    override operator fun Unnamed<FieldDefinition>.unaryPlus() {
        structFieldsUnnamed += this
    }

    override fun build(): StructDefinition {
        val asNamespace = this.namespace.value + this.name
        return StructDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            structFields = this.structFieldsUnnamed.map {
                it.get(asNamespace)
            },
        )
    }
}

@Marker1
fun struct(
    block: StructDefinitionBuilder.() -> Unit = {}
): Unnamed<StructDefinition> {
    return Unnamed { namespace, name ->
        StructDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val struct = struct()

@Marker1
fun struct(
    vararg fields: FieldDefinition,
    block: StructDefinitionBuilder.() -> Unit = {}
): Unnamed<StructDefinition> {
    return struct { +fields.asList(); block() }
}

@Marker1
fun struct(
    vararg fields: Unnamed<FieldDefinition>,
    block: StructDefinitionBuilder.() -> Unit = {}
): Unnamed<StructDefinition> {
    return struct { +fields.asList(); block() }
}

////////////////////////////////////////
