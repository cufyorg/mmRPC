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

sealed interface TypeUnion : Type {
    val unionList: List<Type>

    override fun collectChildren() =
        sequence { yieldAll(unionList.asSequence().flatMap { it.collect() }) }
}

abstract class TypeUnionBuilder {
    abstract operator fun Type.unaryPlus()

    abstract fun build(): TypeUnion
}

////////////////////////////////////////

data class TypeUnionDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val isInline: Boolean,
    override val description: String,
    override val unionList: List<TypeDefinition>,
) : TypeUnion, TypeDefinition {
    override fun collectChildren() =
        sequence { yieldAll(unionList.asSequence().flatMap { it.collect() }) }
}

open class TypeUnionDefinitionBuilder : TypeUnionBuilder() {
    open lateinit var name: String
    open lateinit var namespace: Namespace

    // language=markdown
    open var description = ""

    open operator fun String.unaryPlus() {
        description += this
    }

    protected open val unionList = mutableListOf<TypeDefinition>()
    protected open val anonymousUnionList = mutableListOf<AnonymousType>()

    override operator fun Type.unaryPlus() {
        when (this) {
            is TypeDefinition -> unionList.add(this)
            is AnonymousType -> anonymousUnionList.add(this)
        }
    }

    override fun build(): TypeUnionDefinition {
        val asNamespace = this.namespace + this.name
        return TypeUnionDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = false,
            description = this.description,
            unionList = this.unionList +
                    this.anonymousUnionList.map {
                        it.createDefinition(asNamespace)
                    }
        )
    }
}

@Marker1
fun union(block: TypeUnionDefinitionBuilder.() -> Unit = {}): Unnamed<TypeUnionDefinition> {
    return Unnamed { namespace, name ->
        TypeUnionDefinitionBuilder()
            .also { it.name = name }
            .also { it.namespace = namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

data class AnonymousTypeUnion(
    override val unionList: List<Type>
) : TypeUnion, AnonymousType {
    override fun createDefinition(namespace: Namespace): TypeUnionDefinition {
        val name = "(anonymous|)"
        val asNamespace = namespace + name
        return TypeUnionDefinition(
            name = name,
            namespace = namespace,
            description = "",
            isInline = true,
            unionList = this.unionList.map {
                when (it) {
                    is TypeDefinition -> it
                    is AnonymousType -> it.createDefinition(asNamespace)
                }
            },
        )
    }

    operator fun provideDelegate(t: Any?, p: KProperty<*>): Unnamed<TypeUnionDefinition> {
        return Unnamed { namespace, name ->
            val asNamespace = namespace + name
            TypeUnionDefinition(
                name = name,
                namespace = namespace,
                description = "",
                isInline = false,
                unionList = this.unionList.map {
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
fun union(vararg unionList: Type) = AnonymousTypeUnion(unionList.asList())

////////////////////////////////////////
