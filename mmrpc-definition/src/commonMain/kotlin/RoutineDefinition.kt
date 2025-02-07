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
@SerialName("routine")
data class RoutineDefinition(
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),

    val comm: List<Comm> = emptyList(),
    val faults: List<FaultDefinition> = emptyList(),
    val input: StructDefinition,
    val output: StructDefinition,
) : ElementDefinition() {
    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(faults.asSequence().flatMap { it.collect() })
        yieldAll(input.collect())
        yieldAll(output.collect())
    }
}

open class RoutineDefinitionBuilder :
    ElementDefinitionBuilder() {
    open val comm = mutableListOf<Comm>()
    protected open val faults = mutableListOf<Unnamed<FaultDefinition>>()
    protected open val input = mutableListOf<StructDefinitionBuilder.() -> Unit>()
    protected open val output = mutableListOf<StructDefinitionBuilder.() -> Unit>()

////////////////////////////////////////

    operator fun Comm.unaryPlus() {
        comm += this
    }

////////////////////////////////////////

    @Suppress("INAPPLICABLE_JVM_NAME")
    operator fun Unnamed<FaultDefinition>.unaryPlus() {
        faults += this
    }

    @JvmName("unaryPlusIterableUnnamedFaultDefinition")
    operator fun Iterable<Unnamed<FaultDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusFaultDefinition")
    operator fun FaultDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableFaultDefinition")
    operator fun Iterable<FaultDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

////////////////////////////////////////

    @Marker0
    open fun input(block: StructDefinitionBuilder.() -> Unit) {
        input += block
    }

    @Marker0
    open fun output(block: StructDefinitionBuilder.() -> Unit) {
        output += block
    }

////////////////////////////////////////

    override fun build(): RoutineDefinition {
        val canonicalName = CanonicalName(this.namespace, this.name)
        return RoutineDefinition(
            canonicalName = canonicalName,
            description = this.description,
            metadata = this.metadata.toList(),
            comm = this.comm.toList(),
            faults = this.faults.mapIndexed { i, it ->
                it.get(canonicalName, name = "fault$i")
            },
            input = this.input.let { blocks ->
                if (blocks.isEmpty()) builtin.Void
                else StructDefinitionBuilder()
                    .also { it.name = "Input" }
                    .also { it.namespace = canonicalName }
                    .apply { for (it in blocks) it() }
                    .build()
            },
            output = this.output.let { blocks ->
                if (blocks.isEmpty()) builtin.Void
                else StructDefinitionBuilder()
                    .also { it.name = "Output" }
                    .also { it.namespace = canonicalName }
                    .apply { for (it in blocks) it() }
                    .build()
            },
        )
    }
}

////////////////////////////////////////
