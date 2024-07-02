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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

////////////////////////////////////////

@JvmInline
@Serializable
value class HttpSecurity(val name: String)

@JvmInline
@Serializable
value class HttpMethod(val name: String)

@JvmInline
@Serializable
value class HttpPath(val value: String)

object Http {
    /**
     * The client is considered authenticated with itself
     * as the subject when it provides an access token
     * using [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750)
     * with either its client id in the `client_id` claim
     * and the `iss` claim is a trusted subject identity
     * provider or its client id in the `sub` claim and
     * the `iss` is a trusted client identity provider.
     */
    val SameClient = HttpSecurity("SameClient")

    /**
     * The client is considered authenticated with some
     * subject in some issuer when it provides an access token
     * using [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750)
     * with the subject matching the `sub` claim and the
     * subject issuer matching the `iss` claim.
     */
    val SameSubject = HttpSecurity("SameSubject")

    /**
     * Http GET method. This method is pure in nature and the
     * go-to method for pure functions.
     *
     * ### Input
     *
     * Parameters are passed in (www-url-encoded) request
     * url query with object values encoded in json.
     *
     * ### Output
     *
     * Return value is passed in (json) response body
     * with a success status code.
     *
     * ### Failure
     *
     * If an error is to be returned, an error object composed
     * of the fault canonical name and an optional error message
     * is passed in (json) response body with an error status code:
     *
     * ```json
     * {
     *      "type": <fault-canonical-name>,
     *      "message": <optional-error-message>
     * }
     * ```
     */
    val GET = HttpMethod("GET")

    /**
     * Http POST method. Versatile method and the fallback
     * function if a method choice decision was not made.
     *
     * ### Input
     *
     * Parameters are passed in (json) request body.
     *
     * ### Output
     *
     * Return value is passed in (json) response body
     * with a success status code.
     *
     * ### Failure
     *
     * If an error is to be returned, an error object composed
     * of the fault canonical name and an optional error message
     * is passed in (json) response body with an error status code:
     *
     * ```json
     * {
     *      "type": <fault-canonical-name>,
     *      "message": <optional-error-message>
     * }
     * ```
     */
    val POST = HttpMethod("POST")

    /**
     * Http PUT method. The choice for upsert operations.
     *
     * ### Input
     *
     * Parameters are passed in (json) request body.
     *
     * ### Output
     *
     * Return value is passed in (json) response body
     * with a success status code.
     *
     * ### Failure
     *
     * If an error is to be returned, an error object composed
     * of the fault canonical name and an optional error message
     * is passed in (json) response body with an error status code:
     *
     * ```json
     * {
     *      "type": <fault-canonical-name>,
     *      "message": <optional-error-message>
     * }
     * ```
     */
    val PUT = HttpMethod("PUT")

    /**
     * Http PATCH method. Update an already existing entity.
     *
     * ### Input
     *
     * Parameters are passed in (json) request body.
     *
     * ### Output
     *
     * Return value is passed in (json) response body
     * with a success status code.
     *
     * ### Failure
     *
     * If an error is to be returned, an error object composed
     * of the fault canonical name and an optional error message
     * is passed in (json) response body with an error status code:
     *
     * ```json
     * {
     *      "type": <fault-canonical-name>,
     *      "message": <optional-error-message>
     * }
     * ```
     */
    val PATCH = HttpMethod("PATCH")

    /**
     * Http DELETE method. Delete an entity.
     *
     * ### Input
     *
     * Parameters are passed in (www-url-encoded) request
     * url query with object values encoded in json.
     *
     * ### Output
     *
     * Return value is passed in (json) response body
     * with a success status code.
     *
     * ### Failure
     *
     * If an error is to be returned, an error object composed
     * of the fault canonical name and an optional error message
     * is passed in (json) response body with an error status code:
     *
     * ```json
     * {
     *      "type": <fault-canonical-name>,
     *      "message": <optional-error-message>
     * }
     * ```
     */
    val DELETE = HttpMethod("DELETE")
}

fun Namespace.toHttpPath(): HttpPath {
    return HttpPath(
        value = "/" + segments.joinToString("/")
    )
}

////////////////////////////////////////

@Serializable
@SerialName("http_endpoint")
data class HttpEndpointDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: HttpPath = namespace.toHttpPath(),
    @SerialName("endpoint_method_union")
    val endpointMethodUnion: List<HttpMethod> = listOf(
        Http.POST,
    ),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<HttpSecurity> = emptyList(),
) : EndpointDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<http_endpoint>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
    }
}

open class HttpEndpointDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = HttpEndpointDefinition.ANONYMOUS_NAME

    open var path: String? = null

    protected open var endpointMethodUnion = mutableSetOf<HttpMethod>()
    protected open var endpointSecurityInter = mutableSetOf<HttpSecurity>()

    open operator fun HttpMethod.unaryPlus() {
        endpointMethodUnion += this
    }

    open operator fun HttpSecurity.unaryPlus() {
        endpointSecurityInter += this
    }

    override fun build(): HttpEndpointDefinition {
        val asNamespace = this.namespace.value + this.name
        return HttpEndpointDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            endpointPath = this.path
                ?.let { HttpPath(it) }
                ?: this.namespace.value.toHttpPath(),
            endpointMethodUnion = this.endpointMethodUnion.toList(),
            endpointSecurityInter = this.endpointSecurityInter.toList(),
        )
    }
}

@Marker1
fun endpointHttp(
    block: HttpEndpointDefinitionBuilder.() -> Unit = {}
): Unnamed<HttpEndpointDefinition> {
    return Unnamed { namespace, name, isInline ->
        HttpEndpointDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = isInline }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val endpointHttp = endpointHttp()

////////////////////////////////////////

@Marker2
val RoutineDefinitionBuilder.http: Unit
    get() {
        +endpointHttp { name = "http"; +Http.POST }
    }

@Marker2
fun RoutineDefinitionBuilder.http(
    block: HttpEndpointDefinitionBuilder.() -> Unit = {}
) {
    +endpointHttp { name = "http"; +Http.POST; block() }
}

////////////////////////////////////////
