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

fun Namespace.toHttpPath(): HttpPath {
    return HttpPath(
        value = segments.joinToString("/")
    )
}

////////////////////////////////////////

@Serializable
@SerialName("http_endpoint")
data class HttpEndpointDefinition(
    override val name: String = "(anonymous<http_endpoint>)",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val decorators: List<DecoratorDefinition> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: HttpPath = namespace.toHttpPath(),
    @SerialName("endpoint_method_union")
    val endpointMethodUnion: List<HttpMethod> = listOf(
        Http.POST,
    ),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<HttpSecurity> = emptyList(),
) : EndpointDefinition {
    override fun collectChildren() = sequence {
        yieldAll(decorators.asSequence().flatMap { it.collect() })
    }
}

open class HttpEndpointDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = "(anonymous<http_endpoint>)"

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
            decorators = this.decoratorsUnnamed.map {
                it.get(asNamespace)
            },
            endpointPath = this.path
                ?.let { HttpPath(it) }
                ?: asNamespace.toHttpPath(),
            endpointMethodUnion = this.endpointMethodUnion.toList(),
            endpointSecurityInter = this.endpointSecurityInter.toList(),
        )
    }
}

@Marker1
fun endpointHttp(
    block: HttpEndpointDefinitionBuilder.() -> Unit = {}
): Unnamed<HttpEndpointDefinition> {
    return Unnamed { namespace, name ->
        HttpEndpointDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
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
        +endpointHttp { +Http.POST }
    }

@Marker2
fun RoutineDefinitionBuilder.http(
    block: HttpEndpointDefinitionBuilder.() -> Unit = {}
) {
    +endpointHttp { +Http.POST; block() }
}

////////////////////////////////////////
