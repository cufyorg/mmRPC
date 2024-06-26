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
value class IframeSecurity(val name: String)

@JvmInline
@Serializable
value class IframePath(val value: String)

object Iframe {
    val SameClient = IframeSecurity("SameClient")
}

fun Namespace.toIframePath(): IframePath {
    return IframePath(
        value = "/" + segments.joinToString("/")
    )
}

////////////////////////////////////////

@Serializable
@SerialName("iframe_endpoint")
data class IframeEndpointDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<Metadata> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: IframePath = namespace.toIframePath(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<IframeSecurity> = emptyList(),
) : EndpointDefinition {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<iframe_endpoint>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
    }
}

open class IframeEndpointDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = IframeEndpointDefinition.ANONYMOUS_NAME

    open var path: String? = null

    protected open var endpointSecurityInter = mutableSetOf<IframeSecurity>()

    open operator fun IframeSecurity.unaryPlus() {
        endpointSecurityInter += this
    }

    override fun build(): IframeEndpointDefinition {
        val asNamespace = this.namespace.value + this.name
        return IframeEndpointDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            endpointPath = this.path
                ?.let { IframePath(it) }
                ?: this.namespace.value.toIframePath(),
            endpointSecurityInter = this.endpointSecurityInter.toList(),
        )
    }
}

@Marker1
fun endpointIframe(
    block: IframeEndpointDefinitionBuilder.() -> Unit = {}
): Unnamed<IframeEndpointDefinition> {
    return Unnamed { namespace, name ->
        IframeEndpointDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val endpointIframe = endpointIframe()

////////////////////////////////////////

@Marker2
val RoutineDefinitionBuilder.iframe: Unit
    get() {
        +endpointIframe
    }

@Marker2
fun RoutineDefinitionBuilder.iframe(
    block: IframeEndpointDefinitionBuilder.() -> Unit = {}
) {
    +endpointIframe { block() }
}

////////////////////////////////////////
