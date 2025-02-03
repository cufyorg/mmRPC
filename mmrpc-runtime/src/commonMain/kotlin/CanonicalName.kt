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

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

fun CanonicalName(segments: Collection<String>): CanonicalName {
    require(segments.isNotEmpty()) {
        "CanonicalName must have at least one segment."
    }
    return CanonicalName(segments.joinToString("."))
}

fun CanonicalName(namespace: CanonicalName?, segment: String): CanonicalName {
    return if (namespace == null) CanonicalName(segment) else namespace + segment
}

fun CanonicalName(namespace: CanonicalName?, segments: Collection<String>): CanonicalName {
    return if (namespace == null) CanonicalName(segments) else namespace + segments
}

@JvmInline
@Serializable
value class CanonicalName(val value: String) : Comparable<CanonicalName> {
    init {
        require(value.matches(REGEXP)) {
            "CanonicalName should match: `${REGEXP.pattern}` but got: `${value}`"
        }
    }

    companion object {
        val SEGMENT_REGEXP = Regex("[A-Za-z][A-Za-z0-9_-]*")
        val REGEXP = Regex("([A-Za-z][A-Za-z0-9_-]*)(\\.[A-Za-z][A-Za-z0-9_-]*)*")
    }

    inline val name get() = value.substringAfterLast(".")
    inline val namespace get() = dropLastOrNull(1)

    fun segments() = value.split(".")
    fun segmentsCount() = 1 + value.count { it == '.' }

    override fun compareTo(other: CanonicalName) =
        value.compareTo(other.value)

    /**
     * Return `true` if [canonicalName] contains `this` as a
     * prefix and is not equal to `this`.
     */
    operator fun contains(canonicalName: CanonicalName): Boolean {
        val aStr = this.value
        val bStr = canonicalName.value

        if (aStr.length >= bStr.length)
            return false

        var prevSegEnd = -1
        while (true) {
            val segStart = prevSegEnd + 1
            val aSegEnd = this.value.indexOf('.', segStart)
            val bSegEnd = canonicalName.value.indexOf('.', segStart)

            if (bSegEnd == -1)
                return false

            if (aSegEnd == -1) {
                if (bSegEnd != aStr.length)
                    return false
                for (i in segStart..<aStr.length)
                    if (aStr[i] != bStr[i])
                        return false
                return true
            }

            if (bSegEnd != aSegEnd)
                return false
            for (i in segStart..<aSegEnd)
                if (aStr[i] != bStr[i])
                    return false

            prevSegEnd = aSegEnd
        }
    }

    operator fun plus(segment: String): CanonicalName {
        require(segment matches SEGMENT_REGEXP) {
            "CanonicalName segments should match: `${SEGMENT_REGEXP.pattern}` got: `${segment}`"
        }
        return CanonicalName("$value.$segment")
    }

    operator fun plus(segments: Collection<String>): CanonicalName {
        if (segments.isEmpty()) return this
        require(segments.all { it matches SEGMENT_REGEXP }) {
            "CanonicalName segments should match: `${SEGMENT_REGEXP.pattern}` got: `${segments}`"
        }
        return CanonicalName("$value.${segments.joinToString(".")}")
    }

    /**
     * A sequence of the names from (and including) the root
     * name to (and including) this name. (In order)
     */
    fun collect(): Sequence<CanonicalName> {
        return value.splitToSequence('.')
            .runningReduce { acc, it -> "$acc.$it" }
            .map { CanonicalName(it) }
    }

    /**
     * Return a canonical name from `this` canonical name with
     * only the first [n] segments.
     * Returns `null` if [n] is zero
     *
     * @throws IllegalArgumentException if [n] is negative.
     */
    fun takeOrNull(n: Int): CanonicalName? {
        require(n >= 0) { "Requested element count $n is less than zero." }
        if (n == 0) return null
        // target offset is the offset of the (n+1)th '.' or the end of string
        var dotCount = 0
        var cursor = 0
        while (true) {
            val idx = value.indexOf('.', cursor)
            if (idx == -1) {
                cursor = value.length
                break
            }
            dotCount++
            if (n == dotCount) {
                cursor = idx
                break
            }
            cursor = idx + 1
        }
        return CanonicalName(value.substring(0, cursor))
    }

    /**
     * Return a canonical name from `this` canonical name with
     * the last [n] segments removed.
     * Returns `null` if `this` canonical name does have [n]
     * segments or less.
     *
     * @throws IllegalArgumentException if [n] is negative.
     */
    fun dropLastOrNull(n: Int): CanonicalName? {
        require(n >= 0) { "Requested element count $n is less than zero." }
        if (n == 0) return this
        var dotCount = 0
        var cursor = value.lastIndex
        while (true) {
            val idx = value.lastIndexOf('.', cursor)
            if (idx == -1) return null
            dotCount++
            if (n == dotCount) {
                cursor = idx
                break
            }
            cursor = idx - 1
        }
        return CanonicalName(value.substring(0, cursor))
    }
}
