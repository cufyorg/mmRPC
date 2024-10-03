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
@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

////////////////////////////////////////

@Serializable
@SerialName("kafka_endpoint")
data class KafkaEndpointDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaTopic = namespace.toKafkaTopic(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaSecurity> = listOf(
        Kafka.KafkaACL,
    ),
) : EndpointDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<kafka_endpoint>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
    }
}

open class KafkaEndpointDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = KafkaEndpointDefinition.ANONYMOUS_NAME

    open var topic: String? = null

    protected open var endpointSecurityInter = mutableSetOf<KafkaSecurity>()

    open operator fun KafkaSecurity.unaryPlus() {
        endpointSecurityInter += this
    }

    override fun build(): KafkaEndpointDefinition {
        return KafkaEndpointDefinition(
            name = this.name,
            namespace = this.namespace.value,
            description = this.description,
            metadata = this.metadata.toList(),
            endpointTopic = this.topic
                ?.let { KafkaTopic(it) }
                ?: this.namespace.value.toKafkaTopic(),
            endpointSecurityInter = this.endpointSecurityInter.toList(),
        )
    }
}

@Marker2
fun endpointKafka(
    block: KafkaEndpointDefinitionBuilder.() -> Unit = {},
): Unnamed<KafkaEndpointDefinition> {
    return Unnamed { namespace, name ->
        KafkaEndpointDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
val endpointKafka = endpointKafka()

////////////////////////////////////////

@Marker1
val RoutineDefinitionBuilder.kafka: Unit
    get() {
        +endpointKafka { name = "kafka"; +Kafka.KafkaACL }
    }

@Marker1
fun RoutineDefinitionBuilder.kafka(
    block: KafkaEndpointDefinitionBuilder.() -> Unit = {},
) {
    +endpointKafka { name = "kafka"; +Kafka.KafkaACL; block() }
}

////////////////////////////////////////
