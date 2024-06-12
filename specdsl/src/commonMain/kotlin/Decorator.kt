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

////////////////////////////////////////

data class DecoratorDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val description: String,
) : ElementDefinition {
    override val isInline = false

    override fun collectChildren() =
        emptySequence<ElementDefinition>()
}

open class DecoratorDefinitionBuilder {
    open lateinit var name: String
    open lateinit var namespace: Namespace

    // language=markdown
    open var description = ""

    open operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    open fun build(): DecoratorDefinition {
        return DecoratorDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
        )
    }
}

@Marker1
val decorator = UnnamedProvider { namespace, name ->
    DecoratorDefinitionBuilder()
        .also { it.name = name }
        .also { it.namespace = namespace }
        .build()
}

@Marker1
fun decorator(block: DecoratorDefinitionBuilder.() -> Unit = {}): Unnamed<DecoratorDefinition> {
    return Unnamed { namespace, name ->
        DecoratorDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////
