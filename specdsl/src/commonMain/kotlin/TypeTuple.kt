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

sealed interface TypeTuple : Type {
    val tupleList: List<Type>

    override fun collectChildren() =
        sequence { yieldAll(tupleList.asSequence().flatMap { it.collect() }) }
}

////////////////////////////////////////

data class TypeTupleDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val isInline: Boolean,
    override val description: String,
    override val tupleList: List<TypeDefinition>,
) : TypeTuple, TypeDefinition {
    override fun collectChildren() =
        sequence { yieldAll(tupleList.asSequence().flatMap { it.collect() }) }
}

////////////////////////////////////////

data class AnonymousTypeTuple(
    override val tupleList: List<Type>
) : TypeTuple, AnonymousType {
    override fun createDefinition(namespace: Namespace): TypeTupleDefinition {
        val name = "(anonymous())"
        val asNamespace = namespace + name
        return TypeTupleDefinition(
            name = name,
            namespace = namespace,
            description = "",
            isInline = true,
            tupleList = this.tupleList.map {
                when (it) {
                    is TypeDefinition -> it
                    is AnonymousType -> it.createDefinition(asNamespace)
                }
            },
        )
    }

    operator fun provideDelegate(a: Any?, p: KProperty<*>): Unnamed<TypeTupleDefinition> {
        return Unnamed { namespace, name ->
            val asNamespace = namespace + name
            TypeTupleDefinition(
                name = name,
                namespace = namespace,
                description = "",
                isInline = false,
                tupleList = this.tupleList.map {
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
val tuple = tuple()

@Marker1
fun tuple(vararg tupleList: Type) = AnonymousTypeTuple(tupleList.asList())

////////////////////////////////////////
