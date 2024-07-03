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

object KafkaPublication {
    /**
     * The client is considered authenticated with no
     * subject when it can consume the topic.
     *
     * > This is the set by default for all kafka publication endpoints.
     */
    val KafkaACL = KafkaPublicationSecurity("KafkaACL")
}

fun Namespace.toKafkaPublicationTopic(): KafkaPublicationTopic {
    return KafkaPublicationTopic(
        value = canonicalName.value.replace(":", "-")
    )
}

////////////////////////////////////////
