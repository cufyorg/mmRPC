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

package org.cufy.specdsl

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

////////////////////////////////////////

@JvmInline
@Serializable
value class KafkaPublicationSecurity(val name: String)

@JvmInline
@Serializable
value class KafkaPublicationTopic(val value: String)

/**
 * Kafka publication endpoints are kafka topics that are
 * designed to be produced to by **only the endpoint's server**
 * and consumed by **any privileged endpoint client**.
 *
 * Keys and values are always serialized as `String`.
 *
 * When message header `Content-Type` is set to `application/json`,
 * the values are encoded as **JSON Objects**.
 *
 * When message header `Content-Type` is set to `application/jwt`,
 * the values are encoded as **JWS Compact Serialization**.
 */
object KafkaPublication {
    /**
     * Requires the client to have permission to read
     * from the endpoint's topic.
     *
     * The client is considered authenticated with no
     * subject when it can consume the topic.
     *
     * > This is the set by default for all kafka publication endpoints.
     */
    val KafkaACL = KafkaPublicationSecurity("KafkaACL")

    /**
     * Requires confirmation of the identity of the server.
     *
     * ### JWS MESSAGES
     *
     * The server is considered proved itself when it provides
     * the message as the payload in a jwt signed by a trusted
     * key of the server.
     * The jwt should include the header `kid` which should be
     * the id of either a previously agreed upon key of the
     * server or a key in a previously agreed upon keyset uri.
     */
    val Stamp = KafkaPublicationTopic("Stamp")

    // Planned to add Confidential with JWE
}

fun Namespace.toKafkaPublicationTopic(): KafkaPublicationTopic {
    return KafkaPublicationTopic(
        value = canonicalName.value.replace(":", "-")
    )
}

////////////////////////////////////////
