@file:Suppress("PackageDirectoryMismatch")

package org.cufy.specdsl.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.specdsl.*

@Serializable
@SerialName("kafka_endpoint")
data class CompactKafkaEndpointDefinition(
    override val name: String = KafkaEndpointDefinition.ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<CompactMetadata> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaTopic = namespace.toKafkaTopic(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaSecurity> = listOf(
        Kafka.KafkaACL,
    ),
    @SerialName("endpoint_key.ref")
    val endpointKey: CanonicalName? = null,
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
        endpointKey = this.endpointKey?.canonicalName,
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
            endpointKey = this.endpointKey?.let {
                val item = onLookup(it) ?: return@it null
                require(item is TupleDefinition) {
                    "endpoint_key.ref must point to a TupleDefinition"
                }
                item
            },
        )
    }
}
