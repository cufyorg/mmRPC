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
@SerialName("protocol")
data class ProtocolDefinition(
    override val name: String = "(anonymous<protocol>)",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val decorators: List<DecoratorDefinition> = emptyList(),
    @SerialName("protocol_routines")
    val protocolRoutines: List<RoutineDefinition> = emptyList(),
) : ElementDefinition {
    override fun collectChildren() = sequence {
        yieldAll(decorators.asSequence().flatMap { it.collect() })
        yieldAll(protocolRoutines.asSequence().flatMap { it.collect() })
    }
}

open class ProtocolDefinitionBuilder :
    RoutineDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = "(anonymous<protocol>)"

    protected open val protocolRoutinesUnnamed = mutableListOf<Unnamed<RoutineDefinition>>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedRoutineDefinition")
    override operator fun Unnamed<RoutineDefinition>.unaryPlus() {
        protocolRoutinesUnnamed += this
    }

    override fun build(): ProtocolDefinition {
        val asNamespace = this.namespace.value + this.name
        return ProtocolDefinition(
            namespace = this.namespace.value,
            name = this.name,
            isInline = this.isInline,
            description = this.description,
            decorators = this.decoratorsUnnamed.map {
                it.get(asNamespace)
            },
            protocolRoutines = this.protocolRoutinesUnnamed.map {
                it.get(asNamespace)
            }
        )
    }
}

@Marker1
fun protocol(
    block: ProtocolDefinitionBuilder.() -> Unit = {}
): Unnamed<ProtocolDefinition> {
    return Unnamed { namespace, name ->
        ProtocolDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val protocol = protocol()

@Marker1
fun protocol(
    vararg routines: RoutineDefinition,
    block: ProtocolDefinitionBuilder.() -> Unit = {}
): Unnamed<ProtocolDefinition> {
    return protocol { +routines.asList(); block() }
}

@Marker1
fun protocol(
    vararg routines: Unnamed<RoutineDefinition>,
    block: ProtocolDefinitionBuilder.() -> Unit = {}
): Unnamed<ProtocolDefinition> {
    return protocol { +routines.asList(); block() }
}

////////////////////////////////////////
