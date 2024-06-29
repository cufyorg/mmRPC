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
@SerialName("const")
data class ConstDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("const_type")
    val constType: TypeDefinition,
    @SerialName("const_value")
    val constValue: Literal,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<const>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(constType.collect())
    }
}

open class ConstDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = ConstDefinition.ANONYMOUS_NAME

    open val type = DomainProperty<TypeDefinition>()
    open lateinit var value: Literal

    override fun build(): ConstDefinition {
        val asNamespace = this.namespace.value + this.name
        return ConstDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            constType = this.type.value.get(asNamespace, name = "type"),
            constValue = this.value,
        )
    }
}

@Marker1
internal fun const(
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return Unnamed { namespace, name ->
        ConstDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
fun const(
    type: TypeDefinition,
    value: Literal = null.literal,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= type
        this.value = value
        block()
    }
}

@Marker1
fun const(
    type: Unnamed<TypeDefinition>,
    value: Literal = null.literal,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= type
        this.value = value
        block()
    }
}

////////////////////////////////////////

@Marker1
fun const(
    value: NullLiteral,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= builtin.Any.optional
        this.value = value
        block()
    }
}

@Marker1
fun const(
    value: BooleanLiteral,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= builtin.Boolean
        this.value = value
        block()
    }
}

@Marker1
fun const(
    value: IntLiteral,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= builtin.Int64
        this.value = value
        block()
    }
}

@Marker1
fun const(
    value: FloatLiteral,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= builtin.Float64
        this.value = value
        block()
    }
}

@Marker1
fun const(
    value: StringLiteral,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= builtin.String
        this.value = value
        block()
    }
}

@Marker1
fun const(
    value: TupleLiteral,
    block: ConstDefinitionBuilder.() -> Unit = {}
): Unnamed<ConstDefinition> {
    return const {
        this.type *= tuple {
        }
        this.value = value
        block()
    }
}

////////////////////////////////////////
