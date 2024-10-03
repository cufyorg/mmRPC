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

@JvmInline
@Serializable
value class CanonicalName(val value: String) : Comparable<CanonicalName> {
    inline val name get() = value.substringAfterLast(".")
    inline val namespace get() = asNamespace.parent
    inline val namespaceOrNull get() = asNamespace.parentOrNull
    inline val asNamespace get() = Namespace(value.split("."))

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
