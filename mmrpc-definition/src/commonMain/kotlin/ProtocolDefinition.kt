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
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),

    val routines: List<RoutineDefinition> = emptyList(),
) : ElementDefinition() {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(routines.asSequence().flatMap { it.collect() })
    }
}

open class ProtocolDefinitionBuilder :
    ElementDefinitionBuilder() {
    protected open val routines = mutableListOf<Unnamed<RoutineDefinition>>()

////////////////////////////////////////

    @JvmName("unaryPlusUnnamedRoutineDefinition")
    operator fun Unnamed<RoutineDefinition>.unaryPlus() {
        routines += this
    }

    @JvmName("unaryPlusIterableUnnamedRoutineDefinition")
    operator fun Iterable<Unnamed<RoutineDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusRoutineDefinition")
    operator fun RoutineDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableRoutineDefinition")
    operator fun Iterable<RoutineDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

    operator fun String.invoke(block: RoutineDefinitionBuilder.() -> Unit) {
        +Unnamed { namespace, _ ->
            RoutineDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace = namespace }
                .apply(block)
                .build()
        }
    }

////////////////////////////////////////

    override fun build(): ProtocolDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return ProtocolDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
            routines = this.routines.mapIndexed { i, it ->
                it.get(canonicalName, name = "routine$i")
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
            .also { it.namespace = namespace }
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
) = protocol { +routines.asList(); block() }

@Marker2
fun protocol(
    vararg routines: Unnamed<RoutineDefinition>,
    block: ProtocolDefinitionBuilder.() -> Unit = {},
) = protocol { +routines.asList(); block() }

////////////////////////////////////////
