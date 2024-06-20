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

data class IframeSecurity(val name: String)

////////////////////////////////////////

interface IframeEndpoint : Endpoint {
    val path: String?
    val securityInter: List<IframeSecurity>

    override fun collectChildren() =
        emptySequence<Element>()
}

abstract class IframeEndpointBuilder {
    abstract var name: String
    abstract var path: String?

    // language=markdown
    abstract var description: String

    operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    abstract operator fun IframeSecurity.unaryPlus()

    abstract fun build(): IframeEndpoint
}

////////////////////////////////////////

data class IframeEndpointDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val path: String?,
    override val securityInter: List<IframeSecurity>,
    override val description: String,
) : IframeEndpoint, EndpointDefinition {
    override val isInline = false

    override fun collectChildren() =
        emptySequence<ElementDefinition>()
}

////////////////////////////////////////

data class AnonymousIframeEndpoint(
    override val name: String,
    override val path: String?,
    override val securityInter: List<IframeSecurity>,
    override val description: String,
) : IframeEndpoint, AnonymousEndpoint {
    override fun createDefinition(namespace: Namespace): IframeEndpointDefinition {
        return IframeEndpointDefinition(
            name = this.name,
            namespace = namespace,
            path = this.path,
            securityInter = this.securityInter,
            description = this.description,
        )
    }
}

open class AnonymousIframeEndpointBuilder : IframeEndpointBuilder() {
    override var name: String = "iframe"
    override var path: String? = null

    // language=markdown
    override var description = ""

    protected open var securityInter = mutableSetOf<IframeSecurity>()

    override operator fun IframeSecurity.unaryPlus() {
        securityInter.add(this)
    }

    override fun build(): AnonymousIframeEndpoint {
        return AnonymousIframeEndpoint(
            name = this.name,
            path = this.path,
            securityInter = this.securityInter.toList(),
            description = this.description,
        )
    }
}

////////////////////////////////////////

@Marker2
val RoutineBuilder.iframe get() = iframe()

@Marker2
fun RoutineBuilder.iframe(block: IframeEndpointBuilder.() -> Unit = {}) {
    +AnonymousIframeEndpointBuilder()
        .apply(block)
        .build()
}

////////////////////////////////////////
