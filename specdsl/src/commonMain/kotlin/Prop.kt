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

sealed interface Prop : Element {
    val name: String
    val type: Type
    val default: Const?
    val isOptional: Boolean
    val description: String

    override fun collectChildren() = sequence {
        yield(type)
        default?.let { yield(it) }
    }
}

abstract class PropBuilder {
    abstract var name: String
    abstract var type: Type
    abstract var default: Const?
    abstract var isOptional: Boolean

    @Language("Markdown")
    abstract var description: String

    abstract fun build(): Prop
}

////////////////////////////////////////

data class PropDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val type: TypeDefinition,
    override val default: ConstDefinition?,
    override val isOptional: Boolean,
    override val description: String,
) : Prop, ElementDefinition {
    override val isInline = false

    override fun collectChildren() = sequence {
        yield(type)
        default?.let { yield(it) }
    }
}

open class PropDefinitionBuilder : PropBuilder() {
    open lateinit var namespace: Namespace
    override lateinit var name: String
    override lateinit var type: Type

    override var default: Const? = null
    override var isOptional = false

    @Language("Markdown")
    override var description = ""

    override fun build(): PropDefinition {
        val asNamespace = this.namespace + this.name
        return PropDefinition(
            name = this.name,
            namespace = this.namespace,
            type = when (val type = this.type) {
                is TypeDefinition -> type
                is AnonymousType -> type.createDefinition(asNamespace)
            },
            description = this.description,
            default = when (val default = this.default) {
                null -> null
                is ConstDefinition -> default
                is AnonymousConst -> default.createDefinition(asNamespace)
            },
            isOptional = this.isOptional,
        )
    }
}

@Marker1
fun prop(type: Type, block: PropDefinitionBuilder.() -> Unit = {}): Unnamed<PropDefinition> {
    return Unnamed { namespace, name ->
        PropDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .also { it.type = type }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

data class AnonymousProp(
    override val name: String,
    override val type: Type,
    override val default: Const?,
    override val isOptional: Boolean,
    override val description: String,
) : Prop, AnonymousElement {
    override fun createDefinition(namespace: Namespace): PropDefinition {
        val asNamespace = namespace + name
        return PropDefinition(
            name = this.name,
            namespace = namespace,
            type = when (this.type) {
                is TypeDefinition -> this.type
                is AnonymousType -> this.type.createDefinition(asNamespace)
            },
            default = when (this.default) {
                null -> null
                is ConstDefinition -> this.default
                is AnonymousConst -> this.default.createDefinition(asNamespace)
            },
            isOptional = this.isOptional,
            description = this.description,
        )
    }
}

open class AnonymousPropBuilder : PropBuilder() {
    override lateinit var name: String
    override lateinit var type: Type

    override var default: Const? = null
    override var isOptional = false

    @Language("Markdown")
    override var description = ""

    override fun build(): AnonymousProp {
        return AnonymousProp(
            name = this.name,
            type = this.type,
            description = this.description,
            default = this.default,
            isOptional = this.isOptional,
        )
    }
}

@Marker1
fun prop(
    name: String,
    type: Type,
    default: ConstDefinition? = null,
    isOptional: Boolean = false,
    description: String = "",
): AnonymousProp {
    return AnonymousProp(
        name = name,
        type = type,
        default = default,
        isOptional = isOptional,
        description = description,
    )
}

////////////////////////////////////////
