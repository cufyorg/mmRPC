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
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("routine_endpoints")
    val routineEndpoints: List<EndpointDefinition> = emptyList(),
    @SerialName("routine_fault_union")
    val routineFaultUnion: List<FaultDefinition> = emptyList(),
    @SerialName("routine_input")
    val routineInput: StructDefinition = StructDefinition.Empty,
    @SerialName("routine_output")
    val routineOutput: StructDefinition = StructDefinition.Empty,
) : ElementDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<routine>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(routineEndpoints.asSequence().flatMap { it.collect() })
        yieldAll(routineFaultUnion.asSequence().flatMap { it.collect() })
        yieldAll(routineInput.collect())
        yieldAll(routineOutput.collect())
    }
}

open class RoutineDefinitionBuilder :
    EndpointDefinitionSetDomainContainer,
    FaultDefinitionSetDomainContainer,
    ElementDefinitionBuilder() {
    override var name = RoutineDefinition.ANONYMOUS_NAME

    protected open val routineEndpointsUnnamed = mutableListOf<Unnamed<EndpointDefinition>>()
    protected open val routineFaultUnionUnnamed = mutableListOf<Unnamed<FaultDefinition>>()

    protected open val routineInputBlocks = mutableListOf<StructDefinitionBuilder.() -> Unit>()
    protected open val routineOutputBlocks = mutableListOf<StructDefinitionBuilder.() -> Unit>()

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedEndpointDefinition")
    override operator fun Unnamed<EndpointDefinition>.unaryPlus() {
        routineEndpointsUnnamed += this
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("unaryPlusUnnamedFaultDefinition")
    override operator fun Unnamed<FaultDefinition>.unaryPlus() {
        routineFaultUnionUnnamed += this
    }

    @Marker0
    open fun input(block: StructDefinitionBuilder.() -> Unit) {
        routineInputBlocks += block
    }

    @Marker0
    open fun output(block: StructDefinitionBuilder.() -> Unit) {
        routineOutputBlocks += block
    }

    override fun build(): RoutineDefinition {
        val asNamespace = this.namespace.value + this.name
        return RoutineDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            routineEndpoints = this.routineEndpointsUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "endpoint$i")
            },
            routineFaultUnion = this.routineFaultUnionUnnamed.mapIndexed { i, it ->
                it.get(asNamespace, name = "fault$i")
            },
            routineInput = this.routineInputBlocks.let { blocks ->
                StructDefinitionBuilder()
                    .also { it.name = "Input" }
                    .also { it.namespace *= asNamespace }
                    .apply { for (it in blocks) it() }
                    .build()
            },
            routineOutput = this.routineOutputBlocks.let { blocks ->
                StructDefinitionBuilder()
                    .also { it.name = "Output" }
                    .also { it.namespace *= asNamespace }
                    .apply { for (it in blocks) it() }
                    .build()
            },
        )
    }
}

////////////////////////////////////////
