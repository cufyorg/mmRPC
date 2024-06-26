package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("iframe_endpoint")
data class CompactIframeEndpointDefinition(
    override val name: String = "(anonymous<iframe_endpoint>)",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: IframePath = namespace.toIframePath(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<IframeSecurity> = emptyList(),
) : CompactElementDefinition

fun IframeEndpointDefinition.toCompact(): CompactIframeEndpointDefinition {
    return CompactIframeEndpointDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        endpointPath = this.endpointPath,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactIframeEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> IframeEndpointDefinition? {
    return it@{
        IframeEndpointDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            endpointPath = this.endpointPath,
            endpointSecurityInter = this.endpointSecurityInter,
        )
    }
}
