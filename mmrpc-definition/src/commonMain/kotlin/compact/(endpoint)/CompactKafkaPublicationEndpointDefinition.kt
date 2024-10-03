@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("kafka_publication_endpoint")
data class CompactKafkaPublicationEndpointDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaPublicationTopic,
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaPublicationSecurity> = listOf(
        KafkaPublication.KafkaACL,
    ),
) : CompactElementDefinition

fun KafkaPublicationEndpointDefinition.toCompact(): CompactKafkaPublicationEndpointDefinition {
    return CompactKafkaPublicationEndpointDefinition(
        canonicalName = canonicalName,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        endpointTopic = this.endpointTopic,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactKafkaPublicationEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> KafkaPublicationEndpointDefinition? {
    return it@{
        KafkaPublicationEndpointDefinition(
            name = this.name,
            namespace = this.namespace,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            endpointTopic = this.endpointTopic,
            endpointSecurityInter = this.endpointSecurityInter,
        )
    }
}
