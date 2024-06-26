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
@SerialName("inter")
data class InterDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("inter_types")
    val interTypes: List<TypeDefinition>,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous&)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(interTypes.asSequence().flatMap { it.collect() })
    }
}

open class InterDefinitionBuilder :
    TypeDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = InterDefinition.ANONYMOUS_NAME

    protected open val interTypesUnnamed = mutableListOf<Unnamed<TypeDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedTypeDefinition")
    override operator fun Unnamed<TypeDefinition>.unaryPlus() {
        interTypesUnnamed += this
    }

    override fun build(): InterDefinition {
        val asNamespace = this.namespace.value + this.name
        return InterDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            interTypes = this.interTypesUnnamed.map {
                it.get(asNamespace)
            },
        )
    }
}

@Marker1
fun inter(
    block: InterDefinitionBuilder.() -> Unit = {}
): Unnamed<InterDefinition> {
    return Unnamed { namespace, name ->
        InterDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
fun inter(
    vararg types: TypeDefinition,
    block: InterDefinitionBuilder.() -> Unit = {}
): Unnamed<InterDefinition> {
    return inter { +types.asList(); block() }
}

@Marker1
fun inter(
    vararg types: Unnamed<TypeDefinition>,
    block: InterDefinitionBuilder.() -> Unit = {}
): Unnamed<InterDefinition> {
    return inter { +types.asList(); block() }
}

////////////////////////////////////////
