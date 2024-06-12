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

@DslMarker
annotation class Marker0

@DslMarker
annotation class Marker1

@DslMarker
annotation class Marker2

@DslMarker
annotation class Marker3

open class Namespace(vararg val segments: String) {
    abstract inner class Namespace(vararg segments: String) :
        org.cufy.specdsl.Namespace(*this.segments, *segments)

    override fun hashCode() =
        segments.contentHashCode()

    override fun equals(other: Any?) =
        other is Namespace && other.segments contentEquals segments

    override fun toString() =
        "Namespace($canonicalName)"

    val canonicalName by lazy { segments.joinToString(".") }

    operator fun plus(name: String) =
        org.cufy.specdsl.Namespace(*segments, name)

    operator fun plus(namespace: org.cufy.specdsl.Namespace) =
        org.cufy.specdsl.Namespace(*segments, *namespace.segments)
}

class Unnamed<T>(val block: (Namespace, String) -> T) {
    constructor(block: (Namespace) -> T) : this({ ns, _ -> block(ns) })

    private val values = mutableMapOf<Pair<Namespace, String>, T>()

    operator fun getValue(namespace: Namespace, property: KProperty<*>): T =
        values.getOrPut(namespace to property.name) { block(namespace, property.name) }
}

class UnnamedProvider<T>(val block: (Namespace, String) -> T) {
    constructor(block: (Namespace) -> T) : this({ ns, _ -> block(ns) })

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
        Unnamed { namespace, name -> block(namespace, name) }
}

sealed interface Element {
    fun collect(): Sequence<Element> =
        sequenceOf(this) + collectChildren()

    fun collectChildren(): Sequence<Element>
}

sealed interface AnonymousElement : Element {
    fun createDefinition(namespace: Namespace): ElementDefinition
}

sealed interface ElementDefinition : Element {
    val name: String
    val namespace: Namespace
    val description: String

    val canonicalName: String
        get() {
            return if (namespace.segments.isEmpty()) name
            else "${namespace.canonicalName}.$name"
        }

    val isInline: Boolean

    override fun collect(): Sequence<ElementDefinition> =
        sequenceOf(this) + collectChildren()

    override fun collectChildren(): Sequence<ElementDefinition>
}

sealed interface Type : Element

sealed interface AnonymousType : Type, AnonymousElement {
    override fun createDefinition(namespace: Namespace): TypeDefinition
}

sealed interface TypeDefinition : Type, ElementDefinition

interface Endpoint : Element {
    val name: String
    val description: String
}

interface AnonymousEndpoint : Endpoint, AnonymousElement {
    override fun createDefinition(namespace: Namespace): EndpointDefinition
}

interface EndpointDefinition : Endpoint, ElementDefinition
