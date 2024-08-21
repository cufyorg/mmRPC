@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("kafka_endpoint")
data class CompactKafkaEndpointDefinition(
    override val name: String = KafkaEndpointDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaTopic = namespace.toKafkaTopic(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaSecurity> = listOf(
        Kafka.KafkaACL,
    ),
) : CompactElementDefinition

fun KafkaEndpointDefinition.toCompact(): CompactKafkaEndpointDefinition {
    return CompactKafkaEndpointDefinition(
        name = this.name,
        namespace = this.namespace,
        isInline = this.isInline,
        description = this.description,
        metadata = this.metadata
            .map { it.toCompact() },
        endpointTopic = this.endpointTopic,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactKafkaEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?
): () -> KafkaEndpointDefinition? {
    return it@{
        KafkaEndpointDefinition(
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
