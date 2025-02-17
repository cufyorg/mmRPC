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

open class EnumDefinitionBuilder :
    ElementDefinitionBuilder() {
    open val type = DomainProperty<TypeDefinition>()
    protected open val entries = mutableListOf<Unnamed<ConstDefinition>>()

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
