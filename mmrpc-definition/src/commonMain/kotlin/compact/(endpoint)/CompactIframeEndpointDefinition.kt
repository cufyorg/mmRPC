@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("iframe_endpoint")
data class CompactIframeEndpointDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: IframePath,
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<IframeSecurity> = emptyList(),
) : CompactElementDefinition

fun IframeEndpointDefinition.toCompact(): CompactIframeEndpointDefinition {
    return CompactIframeEndpointDefinition(
        canonicalName = canonicalName,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        endpointPath = this.endpointPath,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactIframeEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
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
