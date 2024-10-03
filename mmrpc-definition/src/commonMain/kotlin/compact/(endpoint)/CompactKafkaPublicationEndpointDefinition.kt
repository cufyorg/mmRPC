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
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaPublicationTopic,
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaPublicationSecurity> = listOf(
        KafkaPublication.KafkaACL,
    ),
) : CompactElementDefinition

fun KafkaPublicationEndpointDefinition.toCompact(strip: Boolean = false): CompactKafkaPublicationEndpointDefinition {
    return CompactKafkaPublicationEndpointDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
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
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            endpointTopic = this.endpointTopic,
            endpointSecurityInter = this.endpointSecurityInter,
        )
    }
}
