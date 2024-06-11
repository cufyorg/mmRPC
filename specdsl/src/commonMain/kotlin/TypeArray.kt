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

sealed interface TypeArray : Type {
    val type: Type

    override fun collectChildren() =
        sequence { yield(type) }
}

////////////////////////////////////////

data class TypeArrayDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val type: TypeDefinition,
    override val isInline: Boolean,
    override val description: String,
) : TypeArray, TypeDefinition {
    override fun collectChildren() =
        sequence { yield(type) }
}

////////////////////////////////////////

data class AnonymousTypeArray(
    override val type: Type
) : TypeArray, AnonymousType {
    override fun createDefinition(namespace: Namespace): TypeArrayDefinition {
        val name = "(anonymous[])"
        val asNamespace = namespace + name
        return TypeArrayDefinition(
            name = name,
            namespace = namespace,
            type = when (this.type) {
                is TypeDefinition -> this.type
                is AnonymousType -> this.type.createDefinition(asNamespace)
            },
            isInline = true,
            description = "",
        )
    }

    operator fun provideDelegate(a: Any?, p: KProperty<*>): Unnamed<TypeArrayDefinition> {
        return Unnamed { namespace, name ->
            val asNamespace = namespace + name
            TypeArrayDefinition(
                name = name,
                namespace = namespace,
                type = when (this.type) {
                    is TypeDefinition -> this.type
                    is AnonymousType -> this.type.createDefinition(asNamespace)
                },
                isInline = false,
                description = "",
            )
        }
    }
}

@Marker1
fun array(type: Type) = AnonymousTypeArray(type)

////////////////////////////////////////
