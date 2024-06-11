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

import org.intellij.lang.annotations.Language

////////////////////////////////////////

sealed interface Routine : Element {
    val name: String
    val description: String
    val endpoints: List<Endpoint>
    val fault: FaultDefinitionUnion
    val inputProps: Props
    val outputProps: Props

    override fun collectChildren() = sequence {
        yieldAll(endpoints)
        yieldAll(fault.unionList)
        yield(inputProps)
        yield(outputProps)
    }
}

abstract class RoutineBuilder {
    abstract var name: String

    @Language("Markdown")
    abstract var description: String

    abstract operator fun Endpoint.unaryPlus()
    abstract operator fun FaultDefinition.unaryPlus()

    @Marker3
    abstract fun input(block: PropsBuilder.() -> Unit)

    @Marker3
    abstract fun output(block: PropsBuilder.() -> Unit)

    abstract fun build(): Routine
}

////////////////////////////////////////

data class RoutineDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val description: String,
    override val endpoints: List<EndpointDefinition>,
    override val fault: FaultDefinitionUnion,
    override val inputProps: PropsDefinition,
    override val outputProps: PropsDefinition,
) : Routine, ElementDefinition {
    override val isInline = false

    override fun collectChildren() = sequence {
        yieldAll(endpoints)
        yieldAll(fault.unionList)
        yield(inputProps)
        yield(outputProps)
    }
}

////////////////////////////////////////

data class AnonymousRoutine(
    override val name: String,
    override val description: String,
    override val endpoints: List<Endpoint>,
    override val fault: FaultDefinitionUnion,
    override val inputProps: Props,
    override val outputProps: Props,
) : Routine, AnonymousElement {
    override fun createDefinition(namespace: Namespace): RoutineDefinition {
        val asNamespace = namespace + this.name
        return RoutineDefinition(
            name = this.name,
            namespace = namespace,
            description = this.description,
            endpoints = this.endpoints.map {
                when (it) {
                    is EndpointDefinition -> it
                    is AnonymousEndpoint -> it.createDefinition(asNamespace)
                    else -> error("$it extends Endpoint but does not extend AnonymousEndpoint nor DefinedEndpoint")
                }
            },
            fault = this.fault,
            inputProps = when (this.inputProps) {
                is PropsDefinition -> this.inputProps
                is AnonymousProps -> this.inputProps.createDefinition(asNamespace)
            },
            outputProps = when (this.outputProps) {
                is PropsDefinition -> this.outputProps
                is AnonymousProps -> this.outputProps.createDefinition(asNamespace)
            },
        )
    }
}

open class AnonymousRoutineBuilder : RoutineBuilder() {
    override lateinit var name: String

    @Language("Markdown")
    override var description = ""

    protected open val endpoints = mutableListOf<Endpoint>()
    protected open val faultUnionList = mutableListOf<FaultDefinition>()
    protected open val inputPropsBuilder = AnonymousPropsBuilder()
        .also { it.name = "input" }
    protected open val outputPropsBuilder = AnonymousPropsBuilder()
        .also { it.name = "output" }

    override operator fun Endpoint.unaryPlus() {
        endpoints += this
    }

    override operator fun FaultDefinition.unaryPlus() {
        faultUnionList += this
    }

    @Marker3
    override fun input(block: PropsBuilder.() -> Unit) {
        inputPropsBuilder.apply(block)
    }

    @Marker3
    override fun output(block: PropsBuilder.() -> Unit) {
        outputPropsBuilder.apply(block)
    }

    override fun build(): AnonymousRoutine {
        return AnonymousRoutine(
            name = this.name,
            fault = FaultDefinitionUnion(
                unionList = this.faultUnionList.toList()
            ),
            inputProps = this.inputPropsBuilder.build(),
            outputProps = this.outputPropsBuilder.build(),
            endpoints = this.endpoints.toList(),
            description = this.description,
        )
    }
}

////////////////////////////////////////
