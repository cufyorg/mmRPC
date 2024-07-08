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
@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

////////////////////////////////////////

@JvmInline
@Serializable
value class IframeSecurity(val name: String)

@JvmInline
@Serializable
value class IframePath(val value: String)

/**
 * Iframe endpoints are HTML pages that are designed to be
 * hosted by the endpoint's server and embedded in any page
 * of a privileged client.
 *
 * Communication between iframe
 */
object Iframe {
    /**
     * Requires confirmation of the identity of a client.
     *
     * ### USING `document.referrer`
     *
     * The client is considered authenticated with itself
     * as the subject when the iframe successfully identifies
     * its domain in the iframe's `document.referrer`.
     */
    val SameClient = IframeSecurity("SameClient")
}

fun Namespace.toIframePath(): IframePath {
    return IframePath(
        value = "/" + segments.joinToString("/")
    )
}

////////////////////////////////////////
