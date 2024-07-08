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
package org.cufy.mmrpc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.min

@Serializable(Namespace.Serializer::class)
class Namespace : Comparable<Namespace> {
    companion object {
        val Toplevel = Namespace()

        fun isAnonymousSegment(segment: String): Boolean {
            return segment.startsWith("(anonymous") &&
                    segment.endsWith(")")
        }

        internal fun create0(segments: List<String>): Namespace {
            return Namespace().also { it.segments = segments }
        }
    }

    class Serializer : KSerializer<Namespace> {
        override val descriptor = PrimitiveSerialDescriptor("Namespace", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Namespace) =
            encoder.encodeString(value.segments.joinToString("."))

        override fun deserialize(decoder: Decoder): Namespace =
            create0(decoder.decodeString().split("."))
    }

    var segments: List<String> = emptyList()
        private set

    constructor()

    constructor(vararg segments: String) {
        require(segments.none { it.contains('.') }) {
            "Namespace segments should not contain '.'"
        }
        this.segments = segments.asList()
    }

    constructor(segments: List<String>) {
        require(segments.none { it.contains('.') }) {
            "Namespace segments should not contain '.'"
        }
        this.segments = segments
    }

    constructor(canonicalName: CanonicalName) {
        this.segments = canonicalName.value.split(".")
    }

    override fun equals(other: Any?) =
        other is Namespace && other.segments == this.segments

    override fun hashCode() =
        this.segments.hashCode()

    override fun toString() =
        "Namespace(${this.canonicalName.value})"

    override fun compareTo(other: Namespace): Int {
        repeat(min(this.segments.size, other.segments.size)) { i ->
            val r = this.segments[i].compareTo(other.segments[i])
            if (r != 0) return r
        }

        return this.segments.size - other.segments.size
    }

    inline val parent get() = this.dropLast(1)
    inline val parentOrNull get() = if (this.isToplevel) null else this.dropLast(1)

    inline val name get() = this.nameOrNull ?: ""
    inline val nameOrNull get() = this.segments.lastOrNull()
    val canonicalName by lazy { CanonicalName(this) }

    val isToplevel get() = this.segments.isEmpty()
    val isOnToplevel get() = this.segments.size == 1
    val isAnonymous by lazy { this.segments.any { isAnonymousSegment(it) } }

    operator fun plus(segment: String): Namespace {
        require(!segment.contains('.')) {
            "Namespace should not contain '.'"
        }
        return create0(this.segments + segment)
    }

    operator fun plus(segments: Iterable<String>): Namespace {
        require(segments.none { it.contains('.') }) {
            "Namespace should not contain '.'"
        }
        return create0(this.segments + segments)
    }

    operator fun plus(namespace: Namespace): Namespace {
        return create0(this.segments + namespace.segments)
    }

    fun take(n: Int): Namespace {
        require(n >= 0) { "Requested element count $n is less than zero." }
        if (n == 0) return Toplevel
        if (n >= this.segments.size) return this
        return create0(this.segments.subList(0, n))
    }

    fun dropLast(n: Int): Namespace {
        require(n >= 0) { "Requested element count $n is less than zero." }
        if (n == 0) return this
        if (n >= this.segments.size) return Toplevel
        return create0(this.segments.subList(0, this.segments.size - n))
    }

    fun collect(): Sequence<Namespace> {
        return sequence {
            repeat(segments.size) { i ->
                yield(create0(segments.subList(0, i + 1)))
            }
        }
    }

    operator fun contains(namespace: Namespace): Boolean {
        if (this.segments.size > namespace.segments.size)
            return false

        return this.segments == namespace.segments.subList(0, this.segments.size)
    }
}
