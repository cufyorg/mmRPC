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
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: IframePath,
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<IframeSecurity> = emptyList(),
) : CompactElementDefinition

fun IframeEndpointDefinition.toCompact(strip: Boolean = false): CompactIframeEndpointDefinition {
    return CompactIframeEndpointDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
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
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            endpointPath = this.endpointPath,
            endpointSecurityInter = this.endpointSecurityInter,
        )
    }
}
