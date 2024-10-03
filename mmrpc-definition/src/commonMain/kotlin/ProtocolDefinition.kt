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
@SerialName("protocol")
data class ProtocolDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("protocol_routines")
    val protocolRoutines: List<RoutineDefinition> = emptyList(),
) : ElementDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<protocol>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(protocolRoutines.asSequence().flatMap { it.collect() })
    }
}

open class ProtocolDefinitionBuilder :
    RoutineDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = ProtocolDefinition.ANONYMOUS_NAME

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
            description = this.description,
            metadata = this.metadata.toList(),
            protocolRoutines = this.protocolRoutinesUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "routine$i")
            }
        )
    }
}

@Marker2
fun protocol(
    block: ProtocolDefinitionBuilder.() -> Unit = {},
): Unnamed<ProtocolDefinition> {
    return Unnamed { namespace, name ->
        ProtocolDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
val protocol = protocol()

@Marker2
fun protocol(
    vararg routines: RoutineDefinition,
    block: ProtocolDefinitionBuilder.() -> Unit = {},
): Unnamed<ProtocolDefinition> {
    return protocol { +routines.asList(); block() }
}

@Marker2
fun protocol(
    vararg routines: Unnamed<RoutineDefinition>,
    block: ProtocolDefinitionBuilder.() -> Unit = {},
): Unnamed<ProtocolDefinition> {
    return protocol { +routines.asList(); block() }
}

////////////////////////////////////////
