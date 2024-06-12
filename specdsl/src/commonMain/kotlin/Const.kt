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

import kotlin.reflect.KProperty

////////////////////////////////////////

sealed interface Const : Type {
    val value: String

    override fun collectChildren() =
        emptySequence<Element>()
}

abstract class ConstBuilder {
    abstract var value: String

    abstract fun build(): Const
}

////////////////////////////////////////

data class ConstDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val value: String,
    override val isInline: Boolean,
    override val description: String,
) : Const, TypeDefinition {
    override fun collectChildren() =
        emptySequence<ElementDefinition>()
}

open class ConstDefinitionBuilder : ConstBuilder() {
    open lateinit var name: String
    open lateinit var namespace: Namespace
    override lateinit var value: String

    // language=markdown
    open var description = ""

    open operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    override fun build(): ConstDefinition {
        return ConstDefinition(
            name = this.name,
            namespace = this.namespace,
            value = this.value,
            isInline = false,
            description = this.description,
        )
    }
}

@Marker1
fun const(value: String, block: ConstDefinitionBuilder.() -> Unit = {}): Unnamed<ConstDefinition> {
    return Unnamed { namespace, name ->
        ConstDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .also { it.value = value }
            .apply(block)
            .build()
    }
}

@Marker1
fun stringConst(value: String, block: ConstDefinitionBuilder.() -> Unit = {}): Unnamed<ConstDefinition> {
    return const("\"$value\"", block)
}

////////////////////////////////////////

data class AnonymousConst(
    override val value: String,
) : Const, AnonymousType {
    override fun createDefinition(namespace: Namespace): ConstDefinition {
        val name = this.value.uppercase()
        return ConstDefinition(
            name = name,
            namespace = namespace,
            value = this.value,
            isInline = true,
            description = "",
        )
    }

    operator fun provideDelegate(t: Any?, p: KProperty<*>): Unnamed<ConstDefinition> {
        return Unnamed { namespace, name ->
            ConstDefinition(
                name = name,
                namespace = namespace,
                value = this.value,
                isInline = false,
                description = "",
            )
        }
    }
}

@Marker1
fun const(value: String) = AnonymousConst(value)

@Marker1
fun stringConst(value: String) = const("\"$value\"")

////////////////////////////////////////
