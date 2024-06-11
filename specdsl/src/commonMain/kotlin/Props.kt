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

sealed interface Props : Element {
    val name: String
    val propsList: List<Prop>

    override fun collectChildren() =
        sequence { yieldAll(propsList) }
}

abstract class PropsBuilder {
    abstract var name: String

    abstract operator fun Prop.unaryPlus()

    operator fun String.invoke(type: Type, block: PropBuilder.() -> Unit = {}) {
        +AnonymousPropBuilder()
            .also { it.name = this }
            .also { it.type = type }
            .apply(block)
            .build()
    }

    abstract fun build(): Props
}

////////////////////////////////////////

data class PropsDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val propsList: List<PropDefinition>,
    override val description: String = "",
) : Props, ElementDefinition {
    override val isInline = false

    override fun collectChildren() =
        sequence { yieldAll(propsList) }
}

////////////////////////////////////////

data class AnonymousProps(
    override val name: String,
    override val propsList: List<Prop>
) : Props, AnonymousElement {
    override fun createDefinition(namespace: Namespace): PropsDefinition {
        val asNamespace = namespace + this.name
        return PropsDefinition(
            name = this.name,
            namespace = namespace,
            propsList = this.propsList.map {
                when (it) {
                    is PropDefinition -> it
                    is AnonymousProp -> it.createDefinition(asNamespace)
                }
            }
        )
    }
}

open class AnonymousPropsBuilder : PropsBuilder() {
    override lateinit var name: String

    protected open val propsList = mutableListOf<Prop>()

    override operator fun Prop.unaryPlus() {
        propsList += this
    }

    override fun build(): AnonymousProps {
        return AnonymousProps(
            name = name,
            propsList = this.propsList.toList()
        )
    }
}

////////////////////////////////////////
