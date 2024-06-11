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

data class ProtocolDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val description: String,
    val routines: List<RoutineDefinition>,
) : ElementDefinition {
    override val isInline = false

    override fun collectChildren() =
        sequence { yieldAll(routines.asSequence().flatMap { it.collect() }) }
}

open class ProtocolDefinitionBuilder {
    open lateinit var name: String
    open lateinit var namespace: Namespace

    @Language("Markdown")
    open var description = ""

    protected open val routines = mutableListOf<RoutineDefinition>()
    protected open val anonymousRoutines = mutableListOf<AnonymousRoutine>()

    open operator fun Routine.unaryPlus() {
        when (this) {
            is RoutineDefinition -> routines += this
            is AnonymousRoutine -> anonymousRoutines += this
        }
    }

    open operator fun String.invoke(block: RoutineBuilder.() -> Unit) {
        +AnonymousRoutineBuilder()
            .also { it.name = this }
            .apply(block)
            .build()
    }

    open fun build(): ProtocolDefinition {
        val asNamespace = this.namespace + this.name
        return ProtocolDefinition(
            namespace = this.namespace,
            name = this.name,
            description = this.description,
            routines = this.routines +
                    this.anonymousRoutines.map {
                        it.createDefinition(asNamespace)
                    }
        )
    }
}

@Marker1
val protocol = UnnamedProvider { namespace, name ->
    ProtocolDefinitionBuilder()
        .also { it.name = name }
        .also { it.namespace = namespace }
        .build()
}

@Marker1
fun protocol(block: ProtocolDefinitionBuilder.() -> Unit = {}): Unnamed<ProtocolDefinition> {
    return Unnamed { namespace, name ->
        ProtocolDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////
