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
@SerialName("tuple")
data class TupleDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("tuple_types")
    val tupleTypes: List<TypeDefinition> = emptyList(),
) : TypeDefinition() {
    companion object {
        val Empty = TupleDefinition()
        const val ANONYMOUS_NAME = "(anonymous())"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(tupleTypes.asSequence().flatMap { it.collect() })
    }
}

open class TupleDefinitionBuilder :
    TypeDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = TupleDefinition.ANONYMOUS_NAME

    protected open val tupleTypesUnnamed = mutableListOf<Unnamed<TypeDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedTypeDefinition")
    override operator fun Unnamed<TypeDefinition>.unaryPlus() {
        tupleTypesUnnamed += this
    }

    override fun build(): TupleDefinition {
        val asNamespace = this.namespace.value + this.name
        return TupleDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            tupleTypes = this.tupleTypesUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "type$i")
            },
        )
    }
}

@Marker1
fun tuple(
    block: TupleDefinitionBuilder.() -> Unit = {},
): Unnamed<TupleDefinition> {
    return Unnamed { namespace, name, isInline ->
        TupleDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = isInline }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val tuple = tuple()

@Marker1
fun tuple(
    vararg types: TypeDefinition,
    block: TupleDefinitionBuilder.() -> Unit = {},
): Unnamed<TupleDefinition> {
    return tuple { +types.asList(); block() }
}

@Marker1
fun tuple(
    vararg types: Unnamed<TypeDefinition>,
    block: TupleDefinitionBuilder.() -> Unit = {},
): Unnamed<TupleDefinition> {
    return tuple { +types.asList(); block() }
}

////////////////////////////////////////
