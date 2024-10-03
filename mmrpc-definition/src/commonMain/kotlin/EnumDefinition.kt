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
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    val enumType: TypeDefinition,
    @SerialName("enum_entries")
    val enumEntries: List<ConstDefinition>,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<enum>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(enumEntries.asSequence().flatMap { it.collect() })
    }
}

open class EnumDefinitionBuilder :
    ConstDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = EnumDefinition.ANONYMOUS_NAME

    open val type = DomainProperty<TypeDefinition>()

    protected open val enumEntriesUnnamed = mutableListOf<Unnamed<ConstDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedConstDefinition")
    override operator fun Unnamed<ConstDefinition>.unaryPlus() {
        enumEntriesUnnamed += this
    }

    override fun build(): EnumDefinition {
        val asNamespace = this.namespace.value + this.name
        return EnumDefinition(
            name = this.name,
            namespace = this.namespace.value,
            description = this.description,
            metadata = this.metadata.toList(),
            enumType = this.type.value.get(asNamespace, name = "type"),
            enumEntries = this.enumEntriesUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "entry$i")
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
            .also { it.namespace *= namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
fun enum(
    type: TypeDefinition,
    block: EnumDefinitionBuilder.() -> Unit = {},
): Unnamed<EnumDefinition> {
    return enum { this.type *= type; block() }
}

@Marker2
fun enum(
    type: TypeDefinition,
    vararg entries: ConstDefinition,
    block: EnumDefinitionBuilder.() -> Unit = {},
): Unnamed<EnumDefinition> {
    return enum { this.type *= type; +entries.asList(); block() }
}

@Marker2
fun enum(
    type: TypeDefinition,
    vararg entries: Unnamed<ConstDefinition>,
    block: EnumDefinitionBuilder.() -> Unit = {},
): Unnamed<EnumDefinition> {
    return enum { this.type *= type; +entries.asList(); block() }
}

////////////////////////////////////////
