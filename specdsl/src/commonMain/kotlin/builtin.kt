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

object Http {
    val SameClient = HttpSecurity("SameClient")
    val SameSubject = HttpSecurity("SameSubject")

    /**
     * Http GET method. This method is pure in nature and the
     * go-to method for pure functions.
     *
     * - Parameters are passed in (www-url-encoded) request
     *      url query with object values encoded in json.
     * - Return value is passed in (json) response body.
     */
    val GET = HttpMethod("GET")

    /**
     * Http POST method. Versatile method and the fallback
     * function if a method choice decision was not made.
     *
     * - Parameters are passed in (json) request body.
     * - Return value is passed in (json) response body.
     */
    val POST = HttpMethod("POST")

    /**
     * Http PUT method. The choice for upsert operations.
     *
     * - Parameters are passed in (json) request body.
     * - Return value is passed in (json) response body.
     */
    val PUT = HttpMethod("PUT")

    /**
     * Http PATCH method. Update an already existing entity.
     *
     * - Parameters are passed in (json) request body.
     * - Return value is passed in (json) response body.
     */
    val PATCH = HttpMethod("PATCH")

    /**
     * Http DELETE method. Delete an entity.
     *
     * - Parameters are passed in (www-url-encoded) request
     *      url query with object values encoded in json.
     * - Return value is passed in (json) response body.
     */
    val DELETE = HttpMethod("DELETE")
}

object Iframe {
    val SameClient = IframeSecurity("SameClient")
}

object Kafka {
    val KafkaACL = KafkaSecurity("KafkaACL")
    val SameClient = KafkaSecurity("SameClient")
}

object KafkaPublication {
    val KafkaACL = KafkaPublicationSecurity("KafkaACL")
}

/**
 * Namespace to be used for defining builtins.
 */
@Suppress("ClassName")
object builtin : Namespace() {
    /**
     * Namespace to be used for defining builtin faults.
     */
    object fault : Namespace()

    /**
     * Namespace to be used for defining builtin props.
     */
    object prop : Namespace()

    /**
     * Namespace to be used for defining builtin decorators.
     */
    @Deprecated("Use builtin.MyDecorator instead of builtin.decorator.MyDecorator")
    object decorator : Namespace()

    /**
     * Namespace to be used for defining builtin serialization objects.
     */
    object serial : Namespace()

    /**
     * Namespace to be used for defining builtin references.
     */
    @Deprecated("builtin.reference was intended for token declarations. Thus, please use builtin.token")
    object reference : Namespace()

    /**
     * Namespace to be used for defining builtin token.
     */
    object token : Namespace()
}
