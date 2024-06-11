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
import kotlin.reflect.KProperty

////////////////////////////////////////

sealed interface TypeInter : Type {
    val interList: List<Type>

    override fun collectChildren() =
        sequence { yieldAll(interList.asSequence().flatMap { it.collect() }) }
}

abstract class TypeInterBuilder {
    abstract operator fun Type.unaryPlus()

    abstract fun build(): TypeInter
}

////////////////////////////////////////

data class TypeInterDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val isInline: Boolean,
    override val description: String,
    override val interList: List<TypeDefinition>,
) : TypeInter, TypeDefinition {
    override fun collectChildren() =
        sequence { yieldAll(interList.asSequence().flatMap { it.collect() }) }
}

open class TypeInterDefinitionBuilder : TypeInterBuilder() {
    open lateinit var name: String
    open lateinit var namespace: Namespace

    @Language("Markdown")
    open var description = ""

    protected open val interList = mutableListOf<TypeDefinition>()
    protected open val anonymousInterList = mutableListOf<AnonymousType>()

    override operator fun Type.unaryPlus() {
        when (this) {
            is TypeDefinition -> interList.add(this)
            is AnonymousType -> anonymousInterList.add(this)
        }
    }

    override fun build(): TypeInterDefinition {
        val asNamespace = this.namespace + this.name
        return TypeInterDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = false,
            description = this.description,
            interList = this.interList +
                    this.anonymousInterList.map {
                        it.createDefinition(asNamespace)
                    }
        )
    }
}

@Marker1
fun inter(block: TypeInterDefinitionBuilder.() -> Unit = {}): Unnamed<TypeInterDefinition> {
    return Unnamed { namespace, name ->
        TypeInterDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

data class AnonymousTypeInter(
    override val interList: List<Type>,
) : TypeInter, AnonymousType {
    override fun createDefinition(namespace: Namespace): TypeInterDefinition {
        val name = "(anonymous&)"
        val asNamespace = namespace + name
        return TypeInterDefinition(
            name = name,
            namespace = namespace,
            isInline = true,
            description = "",
            interList = this.interList.map {
                when (it) {
                    is TypeDefinition -> it
                    is AnonymousType -> it.createDefinition(asNamespace)
                }
            },
        )
    }

    operator fun provideDelegate(t: Any?, p: KProperty<*>): Unnamed<TypeInterDefinition> {
        return Unnamed { namespace, name ->
            val asNamespace = namespace + name
            TypeInterDefinition(
                name = name,
                namespace = namespace,
                isInline = false,
                description = "",
                interList = this.interList.map {
                    when (it) {
                        is TypeDefinition -> it
                        is AnonymousType -> it.createDefinition(asNamespace)
                    }
                },
            )
        }
    }
}

@Marker1
fun inter(vararg interList: Type) = AnonymousTypeInter(interList.asList())

////////////////////////////////////////
