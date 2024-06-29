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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty

////////////////////////////////////////

@Serializable
sealed class ElementDefinition {
    abstract val name: String
    abstract val namespace: Namespace

    @SerialName("is_inline")
    abstract val isInline: Boolean
    abstract val description: String
    abstract val metadata: List<Metadata>

    val canonicalName by lazy { CanonicalName(namespace, name) }
    val isAnonymous by lazy { namespace.isAnonymous || Namespace.isAnonymousSegment(name) }

    fun collect() = sequenceOf(this) + collectChildren()

    abstract fun collectChildren(): Sequence<ElementDefinition>
}

@Serializable
sealed class TypeDefinition : ElementDefinition()

@Serializable
sealed class EndpointDefinition : ElementDefinition()

////////////////////////////////////////

@JvmInline
@Serializable
value class CanonicalName(val value: String) : Comparable<CanonicalName> {
    val asNamespace get() = Namespace(this)

    override fun compareTo(other: CanonicalName) =
        value.compareTo(other.value)
}

fun CanonicalName(namespace: Namespace): CanonicalName {
    return CanonicalName(namespace.segments.joinToString("."))
}

fun CanonicalName(namespace: Namespace, name: String): CanonicalName {
    return CanonicalName(buildString {
        for (segment in namespace.segments) {
            append(segment)
            append(".")
        }

        append(name)
    })
}

////////////////////////////////////////

abstract class NamespaceObject {
    var namespace: Namespace
        protected set

    constructor() {
        this.namespace = Namespace(inferSegment())
    }

    constructor(vararg segments: String) {
        this.namespace = Namespace(*segments)
    }

    constructor(parent: NamespaceObject) {
        this.namespace = parent.namespace + inferSegment()
    }

    constructor(parent: NamespaceObject, vararg segments: String) {
        this.namespace = parent.namespace + segments.asList()
    }

    private fun inferSegment(): String {
        return this::class.simpleName.orEmpty()
    }
}

////////////////////////////////////////

class Unnamed<out T>(private val block: (Namespace, String?) -> T) {
    constructor(block: (Namespace) -> T) : this({ ns, _ -> block(ns) })
    constructor(value: T) : this({ _, _ -> value })

    fun get(namespace: Namespace, name: String) =
        block(namespace, name)

    fun get(namespace: Namespace) =
        block(namespace, null)

    fun get(obj: NamespaceObject, name: String) =
        block(obj.namespace, name)

    fun get(obj: NamespaceObject) =
        block(obj.namespace, null)

    private val values = mutableMapOf<Pair<Namespace, String?>, T>()

    operator fun getValue(namespace: Namespace, property: KProperty<*>): T {
        return values.getOrPut(namespace to property.name) {
            val splits = property.name.split("__")
            val ns = namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n)
        }
    }

    operator fun getValue(obj: NamespaceObject, property: KProperty<*>): T {
        return values.getOrPut(obj.namespace to property.name) {
            val splits = property.name.split("__")
            val ns = obj.namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n)
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Unnamed<T> {
        return Unnamed { namespace, name -> block(namespace, name) }
    }
}

@DslMarker
annotation class Marker0

@DslMarker
annotation class Marker1

@DslMarker
annotation class Marker2

@DslMarker
annotation class Marker3

////////////////////////////////////////
