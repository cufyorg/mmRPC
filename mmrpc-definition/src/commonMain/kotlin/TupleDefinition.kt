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
@SerialName("tuple")
data class TupleDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),

    val types: List<TypeDefinition> = emptyList(),
) : TypeDefinition() {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(types.asSequence().flatMap { it.collect() })
    }
}

open class TupleDefinitionBuilder :
    ElementDefinitionBuilder() {
    protected open val types = mutableListOf<Unnamed<TypeDefinition>>()

////////////////////////////////////////

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedTypeDefinition")
    operator fun Unnamed<TypeDefinition>.unaryPlus() {
        types += this
    }

    @JvmName("unaryPlusIterableUnnamedTypeDefinition")
    operator fun Iterable<Unnamed<TypeDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusTypeDefinition")
    operator fun TypeDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableTypeDefinition")
    operator fun Iterable<TypeDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

////////////////////////////////////////

    override fun build(): TupleDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return TupleDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
            types = this.types.mapIndexed { i, it ->
                it.get(canonicalName, name = "type$i")
            },
        )
    }
}

@Marker2
fun tuple(
    block: TupleDefinitionBuilder.() -> Unit = {},
): Unnamed<TupleDefinition> {
    return Unnamed { namespace, name ->
        TupleDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
val tuple = tuple()

@Marker2
fun tuple(
    vararg types: TypeDefinition,
    block: TupleDefinitionBuilder.() -> Unit = {},
): Unnamed<TupleDefinition> {
    return tuple { +types.asList(); block() }
}

@Marker2
fun tuple(
    vararg types: Unnamed<TypeDefinition>,
    block: TupleDefinitionBuilder.() -> Unit = {},
): Unnamed<TupleDefinition> {
    return tuple { +types.asList(); block() }
}

////////////////////////////////////////
