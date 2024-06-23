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

@JvmInline
@Serializable
value class SpecSheet(val elements: List<ElementDefinition>) {
    constructor(vararg elements: ElementDefinition) : this(elements.asList())
}

@JvmInline
@Serializable
value class Namespace(val segments: List<String>) {
    companion object {
        val Toplevel = Namespace()
    }

    constructor(vararg segments: String) : this(segments.asList())

    override fun toString() = "Namespace($canonicalName)"

    val canonicalName get() = this.segments.joinToString(".")

    operator fun plus(segment: String) =
        Namespace(segments = this.segments + segment)

    operator fun plus(segments: Iterable<String>) =
        Namespace(segments = this.segments + segments)

    operator fun plus(namespace: Namespace) =
        Namespace(segments = this.segments + namespace.segments)

    operator fun contains(namespace: Namespace): Boolean {
        if (this.segments.size > namespace.segments.size)
            return false

        return this.segments == namespace.segments.subList(0, this.segments.size)
    }
}

@Serializable
sealed interface ElementDefinition {
    val name: String
    val namespace: Namespace

    @SerialName("is_inline")
    val isInline: Boolean
    val description: String
    val decorators: List<DecoratorDefinition>

    val canonicalName: String
        get() {
            return if (namespace.segments.isEmpty()) name
            else "${namespace.canonicalName}.$name"
        }

    fun collect() = sequenceOf(this) + collectChildren()

    fun collectChildren(): Sequence<ElementDefinition>
}

@Serializable
sealed interface TypeDefinition : ElementDefinition

@Serializable
sealed interface EndpointDefinition : ElementDefinition
