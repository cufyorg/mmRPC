@file:Suppress("PackageDirectoryMismatch")

package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("http_endpoint")
data class CompactHttpEndpointDefinition(
    override val name: String = HttpEndpointDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("endpoint_path")
    val endpointPath: HttpPath = namespace.toHttpPath(),
    @SerialName("endpoint_method_union")
    val endpointMethodUnion: List<HttpMethod> = listOf(
        Http.POST,
    ),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<HttpSecurity> = emptyList(),
) : CompactElementDefinition

fun HttpEndpointDefinition.toCompact(): CompactHttpEndpointDefinition {
    return CompactHttpEndpointDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        endpointPath = this.endpointPath,
        endpointMethodUnion = this.endpointMethodUnion,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactHttpEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> HttpEndpointDefinition? {
    return it@{
        HttpEndpointDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
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
