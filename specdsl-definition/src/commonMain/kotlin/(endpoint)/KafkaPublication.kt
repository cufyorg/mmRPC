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

package org.cufy.specdsl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

////////////////////////////////////////

@JvmInline
@Serializable
value class KafkaPublicationSecurity(val name: String)

@JvmInline
@Serializable
value class KafkaPublicationTopic(val value: String)

object KafkaPublication {
    /**
     * The client is considered authenticated with no
     * subject when it can consume the topic.
     *
     * > This is the set by default for all kafka publication endpoints.
     */
    val KafkaACL = KafkaPublicationSecurity("KafkaACL")
}

fun Namespace.toKafkaPublicationTopic(): KafkaPublicationTopic {
    return KafkaPublicationTopic(
        value = canonicalName.value.replace(":", "-")
    )
}

////////////////////////////////////////

@Serializable
@SerialName("kafka_publication_endpoint")
data class KafkaPublicationEndpointDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    @SerialName("is_inline")
    override val isInline: Boolean = true,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaPublicationTopic = namespace.toKafkaPublicationTopic(),
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaPublicationSecurity> = listOf(
        KafkaPublication.KafkaACL,
    ),
    /**
     * How to calculate the record key.
     *
     * The key is calculated by taking the `md5` hash of
     * the result of concatenating the string representation
     * of each value in the key tuple with `;` as the separator.
     *
     * This is considered a suggestion and implementations can
     * use other means for calculating the record key other that
     * the specified.
     */
    @SerialName("endpoint_key")
    val endpointKey: TupleDefinition? = null,
) : EndpointDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<kafka_publication_endpoint>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        endpointKey?.let { yieldAll(it.collect()) }
    }
}

open class KafkaPublicationEndpointDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = KafkaPublicationEndpointDefinition.ANONYMOUS_NAME

    open var topic: String? = null
    open val key = OptionalDomainProperty<TupleDefinition>()

    protected open var endpointSecurityInter = mutableSetOf<KafkaPublicationSecurity>()

    open operator fun KafkaPublicationSecurity.unaryPlus() {
        endpointSecurityInter += this
    }

    override fun build(): KafkaPublicationEndpointDefinition {
        val asNamespace = this.namespace.value + this.name
        return KafkaPublicationEndpointDefinition(
            name = this.name,
            namespace = this.namespace.value,
            isInline = this.isInline,
            description = this.description,
            metadata = this.metadata.toList(),
            endpointTopic = this.topic
                ?.let { KafkaPublicationTopic(it) }
                ?: this.namespace.value.toKafkaPublicationTopic(),
            endpointSecurityInter = this.endpointSecurityInter.toList(),
            endpointKey = this.key.value?.get(asNamespace, name = "key"),
        )
    }
}

@Marker1
fun endpointKafkaPublication(
    block: KafkaPublicationEndpointDefinitionBuilder.() -> Unit = {}
): Unnamed<KafkaPublicationEndpointDefinition> {
    return Unnamed { namespace, name, isInline ->
        KafkaPublicationEndpointDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .also { it.isInline = isInline }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker1
val endpointKafkaPublication = endpointKafkaPublication()

////////////////////////////////////////

@Marker2
val RoutineDefinitionBuilder.kafkaPublication: Unit
    get() {
        +endpointKafkaPublication { name = "kafkaPublication"; +KafkaPublication.KafkaACL }
    }

@Marker2
fun RoutineDefinitionBuilder.kafkaPublication(
    block: KafkaPublicationEndpointDefinitionBuilder.() -> Unit = {}
) {
    +endpointKafkaPublication { name = "kafkaPublication"; +KafkaPublication.KafkaACL; block() }
}

////////////////////////////////////////
