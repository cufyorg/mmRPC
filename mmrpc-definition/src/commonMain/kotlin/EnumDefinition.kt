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
import kotlin.jvm.JvmName

////////////////////////////////////////

@Serializable
@SerialName("enum")
data class EnumDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),

    val type: TypeDefinition,
    val entries: List<ConstDefinition>,
) : TypeDefinition() {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(type.collect())
        yieldAll(entries.asSequence().flatMap { it.collect() })
    }
}

class EnumDefinitionBuilder :
    ElementDefinitionBuilder() {
    val type = DomainProperty<TypeDefinition>()
    private val entries = mutableListOf<Unnamed<ConstDefinition>>()

////////////////////////////////////////

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedConstDefinition")
    operator fun Unnamed<ConstDefinition>.unaryPlus() {
        entries += this
    }

    @JvmName("unaryPlusIterableUnnamedConstDefinition")
    operator fun Iterable<Unnamed<ConstDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusConstDefinition")
    operator fun ConstDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableConstDefinition")
    operator fun Iterable<ConstDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

////////////////////////////////////////

    private operator fun String.invoke(
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) {
        +Unnamed { namespace, _ ->
            ConstDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace = namespace }
                .apply(block)
                .build()
        }
    }

////////////////////////////////////////

    operator fun String.invoke(
        type: TypeDefinition,
        value: Literal = null.literal,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this { this.type *= type; this.value = value; block() }

    operator fun String.invoke(
        type: Unnamed<TypeDefinition>,
        value: Literal = null.literal,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this { this.type *= type; this.value = value; block(); }

////////////////////////////////////////

    operator fun String.invoke(
        value: NullLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(builtin.Any.optional, value, block)

    operator fun String.invoke(
        value: BooleanLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(builtin.Boolean, value, block)

    operator fun String.invoke(
        value: IntLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(builtin.Int64, value, block)

    operator fun String.invoke(
        value: FloatLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(builtin.Float64, value, block)

    operator fun String.invoke(
        value: StringLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(builtin.String, value, block)

    operator fun String.invoke(
        value: TupleLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(tuple, value, block)

    operator fun String.invoke(
        value: StructLiteral,
        block: ConstDefinitionBuilder.() -> Unit = {}
    ) = this(struct, value, block)

////////////////////////////////////////

    override fun build(): EnumDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return EnumDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
            type = this.type.value.get(canonicalName, name = "type"),
            entries = this.entries.mapIndexed { i, it ->
                it.get(canonicalName, name = "entry$i")
            },
        )
    }
}

@Marker2
internal fun enum(
    block: EnumDefinitionBuilder.() -> Unit = {},
): Unnamed<EnumDefinition> {
    return Unnamed { namespace, name ->
        EnumDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
fun enum(
    type: TypeDefinition,
    block: EnumDefinitionBuilder.() -> Unit = {},
) = enum { this.type *= type; block() }

@Marker2
fun enum(
    type: TypeDefinition,
    vararg entries: ConstDefinition,
    block: EnumDefinitionBuilder.() -> Unit = {},
) = enum { this.type *= type; +entries.asList(); block() }

@Marker2
fun enum(
    type: TypeDefinition,
    vararg entries: Unnamed<ConstDefinition>,
    block: EnumDefinitionBuilder.() -> Unit = {},
) = enum { this.type *= type; +entries.asList(); block() }

////////////////////////////////////////
