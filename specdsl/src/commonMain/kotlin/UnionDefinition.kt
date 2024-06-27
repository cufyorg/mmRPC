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
@SerialName("union")
data class UnionDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("union_types")
    val unionTypes: List<TypeDefinition>,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous|)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(unionTypes.asSequence().flatMap { it.collect() })
    }
}

open class UnionDefinitionBuilder :
    TypeDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = UnionDefinition.ANONYMOUS_NAME

    protected open val unionTypesUnnamed = mutableListOf<Unnamed<TypeDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedTypeDefinition")
    override operator fun Unnamed<TypeDefinition>.unaryPlus() {
        unionTypesUnnamed += this
    }

    override fun build(): UnionDefinition {
        val asNamespace = this.namespace.value + this.name
        return UnionDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            unionTypes = this.unionTypesUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "type$i")
            },
        )
    }
}

@Marker1
fun union(
    block: UnionDefinitionBuilder.() -> Unit = {},
): Unnamed<UnionDefinition> {
    return Unnamed { namespace, name ->
        UnionDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
fun union(
    vararg types: TypeDefinition,
    block: UnionDefinitionBuilder.() -> Unit = {},
): Unnamed<UnionDefinition> {
    return union { +types.asList(); block() }
}

@Marker1
fun union(
    vararg types: Unnamed<TypeDefinition>,
    block: UnionDefinitionBuilder.() -> Unit = {},
): Unnamed<UnionDefinition> {
    return union { +types.asList(); block() }
}

////////////////////////////////////////
