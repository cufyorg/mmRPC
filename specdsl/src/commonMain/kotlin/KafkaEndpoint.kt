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

data class KafkaSecurity(val name: String)

data class KafkaSecurityInter(val interList: List<KafkaSecurity>)

////////////////////////////////////////

interface KafkaEndpoint : Endpoint {
    val topic: String?
    val security: KafkaSecurityInter
    val key: TypeTuple?

    override fun collectChildren() =
        sequence { key?.let { yield(it) } }
}

abstract class KafkaEndpointBuilder {
    abstract var name: String
    abstract var topic: String?
    abstract var key: TypeTuple?

    @Language("Markdown")
    abstract var description: String

    abstract operator fun KafkaSecurity.unaryPlus()

    abstract fun build(): KafkaEndpoint
}

////////////////////////////////////////

open class KafkaEndpointDefinition(
    override val name: String,
    override val namespace: Namespace,
    override val topic: String?,
    override val security: KafkaSecurityInter,
    override val key: TypeTupleDefinition?,
    override val description: String,
) : KafkaEndpoint, EndpointDefinition {
    override val isInline = false

    override fun collectChildren() =
        sequence { key?.let { yield(it) } }
}

////////////////////////////////////////

open class AnonymousKafkaEndpoint(
    override val name: String,
    override val topic: String?,
    override val security: KafkaSecurityInter,
    override val key: TypeTuple?,
    override val description: String,
) : KafkaEndpoint, AnonymousEndpoint {
    override fun createDefinition(namespace: Namespace): KafkaEndpointDefinition {
        val asNamespace = namespace + this.name
        return KafkaEndpointDefinition(
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

open class AnonymousKafkaEndpointBuilder : KafkaEndpointBuilder() {
    override var name: String = "kafka"
    override var topic: String? = null
    override var key: TypeTuple? = null

    @Language("Markdown")
    override var description = ""

    protected open var securityInterList = mutableSetOf<KafkaSecurity>()

    override operator fun KafkaSecurity.unaryPlus() {
        securityInterList.add(this)
    }

    override fun build(): AnonymousKafkaEndpoint {
        return AnonymousKafkaEndpoint(
            name = this.name,
            topic = this.topic,
            security = KafkaSecurityInter(
                interList = this.securityInterList.toList()
            ),
            key = this.key,
            description = this.description,
        )
    }
}

@Marker2
val RoutineBuilder.kafka get() = kafka()

@Marker2
fun RoutineBuilder.kafka(block: KafkaEndpointBuilder.() -> Unit = {}) {
    +AnonymousKafkaEndpointBuilder()
        .apply { +Kafka.KafkaACL }
        .apply(block)
        .build()
}

////////////////////////////////////////
