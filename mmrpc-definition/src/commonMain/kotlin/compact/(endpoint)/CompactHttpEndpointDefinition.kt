@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("http_endpoint")
data class CompactHttpEndpointDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: HttpPath,
    @SerialName("endpoint_method_union")
    val endpointMethodUnion: List<HttpMethod> = listOf(
        Http.POST,
    ),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<HttpSecurity> = emptyList(),
) : CompactElementDefinition

fun HttpEndpointDefinition.toCompact(strip: Boolean = false): CompactHttpEndpointDefinition {
    return CompactHttpEndpointDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        endpointPath = this.endpointPath,
        endpointMethodUnion = this.endpointMethodUnion,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactHttpEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> HttpEndpointDefinition? {
    return it@{
        HttpEndpointDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            endpointPath = this.endpointPath,
            endpointMethodUnion = this.endpointMethodUnion,
            endpointSecurityInter = this.endpointSecurityInter,
        )
    }
}
