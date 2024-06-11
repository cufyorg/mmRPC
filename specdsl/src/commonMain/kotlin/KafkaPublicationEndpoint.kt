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

import org.intellij.lang.annotations.Language

////////////////////////////////////////

data class KafkaPublicationSecurity(val name: String)

data class KafkaPublicationSecurityInter(val interList: List<KafkaPublicationSecurity>)

////////////////////////////////////////

interface KafkaPublicationEndpoint : Endpoint {
    val topic: String?
    val security: KafkaPublicationSecurityInter
    val key: TypeTuple?

    override fun collectChildren() =
        sequence { key?.let { yieldAll(it.collect()) } }
}

abstract class KafkaPublicationEndpointBuilder {
    abstract var name: String
    abstract var topic: String?
    abstract var key: TypeTuple?

    @Language("Markdown")
    abstract var description: String

    abstract operator fun KafkaPublicationSecurity.unaryPlus()

    abstract fun build(): KafkaPublicationEndpoint
}

////////////////////////////////////////

data class KafkaPublicationEndpointDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val topic: String?,
    override val security: KafkaPublicationSecurityInter,
    override val key: TypeTupleDefinition?,
    override val description: String,
) : KafkaPublicationEndpoint, EndpointDefinition {
    override val isInline = false

    override fun collectChildren() =
        sequence { key?.let { yieldAll(it.collect()) } }
}

////////////////////////////////////////

data class AnonymousKafkaPublicationEndpoint(
    override val name: String,
    override val topic: String?,
    override val security: KafkaPublicationSecurityInter,
    override val key: TypeTuple?,
    override val description: String,
) : KafkaPublicationEndpoint, AnonymousEndpoint {
    override fun createDefinition(namespace: Namespace): KafkaPublicationEndpointDefinition {
        val asNamespace = namespace + this.name
        return KafkaPublicationEndpointDefinition(
            name = this.name,
            namespace = namespace,
            topic = this.topic,
            security = this.security,
            key = when (val key = this.key) {
                null -> null
                is TypeTupleDefinition -> key
                is AnonymousTypeTuple -> key.createDefinition(asNamespace)
            },
            description = this.description,
        )
    }
}

open class AnonymousKafkaPublicationEndpointBuilder : KafkaPublicationEndpointBuilder() {
    override var name: String = "kafka_publication"
    override var topic: String? = null
    override var key: TypeTuple? = null

    @Language("Markdown")
    override var description = ""

    protected open var securityInterList = mutableSetOf<KafkaPublicationSecurity>()

    override operator fun KafkaPublicationSecurity.unaryPlus() {
        securityInterList.add(this)
    }

    override fun build(): AnonymousKafkaPublicationEndpoint {
        return AnonymousKafkaPublicationEndpoint(
            name = this.name,
            topic = this.topic,
            security = KafkaPublicationSecurityInter(
                interList = this.securityInterList.toList()
            ),
            key = this.key,
            description = this.description,
        )
    }
}

@Marker2
val RoutineBuilder.kafkaPublication get() = kafkaPublication()

@Marker2
fun RoutineBuilder.kafkaPublication(block: KafkaPublicationEndpointBuilder.() -> Unit = {}) {
    +AnonymousKafkaPublicationEndpointBuilder()
        .apply { +KafkaPublication.KafkaACL }
        .apply(block)
        .build()
}

////////////////////////////////////////
