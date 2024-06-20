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

////////////////////////////////////////

data class HttpSecurity(val name: String)

data class HttpMethod(val name: String)

////////////////////////////////////////

interface HttpEndpoint : Endpoint {
    val path: String?
    val methodUnion: List<HttpMethod>
    val securityInter: List<HttpSecurity>

    override fun collectChildren() =
        emptySequence<Element>()
}

abstract class HttpEndpointBuilder {
    abstract var name: String
    abstract var path: String?

    // language=markdown
    abstract var description: String

    operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    abstract operator fun HttpMethod.unaryPlus()

    abstract operator fun HttpSecurity.unaryPlus()

    abstract fun build(): HttpEndpoint
}

////////////////////////////////////////

data class HttpEndpointDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val path: String?,
    override val methodUnion: List<HttpMethod>,
    override val securityInter: List<HttpSecurity>,
    override val description: String,
) : HttpEndpoint, EndpointDefinition {
    override val isInline = false

    override fun collectChildren() =
        emptySequence<ElementDefinition>()
}

////////////////////////////////////////

data class AnonymousHttpEndpoint(
    override val name: String,
    override val path: String?,
    override val methodUnion: List<HttpMethod>,
    override val securityInter: List<HttpSecurity>,
    override val description: String,
) : HttpEndpoint, AnonymousEndpoint {
    override fun createDefinition(namespace: Namespace): HttpEndpointDefinition {
        return HttpEndpointDefinition(
            name = this.name,
            namespace = namespace,
            path = this.path,
            methodUnion = this.methodUnion,
            securityInter = this.securityInter,
            description = this.description,
        )
    }
}

open class AnonymousHttpEndpointBuilder : HttpEndpointBuilder() {
    override var name: String = "http"
    override var path: String? = null

    // language=markdown
    override var description = ""

    protected open var methodUnion = mutableSetOf<HttpMethod>()
    protected open var securityInter = mutableSetOf<HttpSecurity>()

    override operator fun HttpMethod.unaryPlus() {
        methodUnion.add(this)
    }

    override operator fun HttpSecurity.unaryPlus() {
        securityInter.add(this)
    }

    override fun build(): AnonymousHttpEndpoint {
        return AnonymousHttpEndpoint(
            name = this.name,
            path = this.path,
            methodUnion = this.methodUnion.toList(),
            securityInter = this.securityInter.toList(),
            description = this.description,
        )
    }
}

////////////////////////////////////////

@Marker2
val RoutineBuilder.http get() = http()

@Marker2
fun RoutineBuilder.http(block: HttpEndpointBuilder.() -> Unit = {}) {
    +AnonymousHttpEndpointBuilder()
        .apply { +Http.POST }
        .apply(block)
        .build()
}

////////////////////////////////////////
