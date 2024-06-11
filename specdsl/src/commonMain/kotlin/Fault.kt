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

data class FaultDefinitionUnion(val unionList: List<FaultDefinition>)

////////////////////////////////////////

data class FaultDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val description: String,
) : ElementDefinition {
    override val isInline = false

    override fun collectChildren() =
        emptySequence<ElementDefinition>()
}

open class FaultDefinitionBuilder {
    open lateinit var name: String
    open lateinit var namespace: Namespace

    @Language("Markdown")
    open var description = ""

    open fun build(): FaultDefinition {
        return FaultDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
        )
    }
}

@Marker1
val fault = UnnamedProvider { namespace, name ->
    FaultDefinitionBuilder()
        .also { it.name = name }
        .also { it.namespace = namespace }
        .build()
}

@Marker1
fun fault(block: FaultDefinitionBuilder.() -> Unit = {}): Unnamed<FaultDefinition> {
    return Unnamed { namespace, name ->
        FaultDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////
