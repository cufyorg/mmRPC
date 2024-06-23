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
package org.cufy.specdsl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

////////////////////////////////////////

@JvmInline
@Serializable
value class KafkaSecurity(val name: String)

@JvmInline
@Serializable
value class KafkaTopic(val value: String)

object Kafka {
    val KafkaACL = KafkaSecurity("KafkaACL")
    val SameClient = KafkaSecurity("SameClient")
}

fun Namespace.toKafkaTopic(): KafkaTopic {
    return KafkaTopic(
        value = canonicalName.replace(":", "-")
    )
}

////////////////////////////////////////

@Serializable
@SerialName("kafka_endpoint")
data class KafkaEndpointDefinition(
    override val name: String = "(anonymous<kafka_endpoint>)",
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val decorators: List<DecoratorDefinition> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaTopic = namespace.toKafkaTopic(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaSecurity> = listOf(
        Kafka.KafkaACL,
    ),
    @SerialName("endpoint_key")
    val endpointKey: TupleDefinition? = null,
) : EndpointDefinition {
    override fun collectChildren() = sequence {
        yieldAll(decorators.asSequence().flatMap { it.collect() })
        endpointKey?.let { yieldAll(it.collect()) }
    }
}

open class KafkaEndpointDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = "(anonymous<kafka_endpoint>)"

    open var topic: String? = null
    open val key = OptionalDomainProperty<TupleDefinition>()

    protected open var endpointSecurityInter = mutableSetOf<KafkaSecurity>()

    open operator fun KafkaSecurity.unaryPlus() {
        endpointSecurityInter += this
    }

    override fun build(): KafkaEndpointDefinition {
        val asNamespace = this.namespace.value + this.name
        return KafkaEndpointDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            decorators = this.decoratorsUnnamed.map {
                it.get(asNamespace)
            },
            endpointTopic = this.topic
                ?.let { KafkaTopic(it) }
                ?: asNamespace.toKafkaTopic(),
            endpointSecurityInter = this.endpointSecurityInter.toList(),
            endpointKey = this.key.value?.get(asNamespace),
        )
    }
}

@Marker1
fun endpointKafka(
    block: KafkaEndpointDefinitionBuilder.() -> Unit = {}
): Unnamed<KafkaEndpointDefinition> {
    return Unnamed { namespace, name ->
        KafkaEndpointDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = name == null }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val endpointKafka = endpointKafka()

////////////////////////////////////////

@Marker2
val RoutineDefinitionBuilder.kafka: Unit
    get() {
        +endpointKafka { +Kafka.KafkaACL }
    }

@Marker2
fun RoutineDefinitionBuilder.kafka(
    block: KafkaEndpointDefinitionBuilder.() -> Unit = {}
) {
    +endpointKafka { +Kafka.KafkaACL; block() }
}

////////////////////////////////////////
