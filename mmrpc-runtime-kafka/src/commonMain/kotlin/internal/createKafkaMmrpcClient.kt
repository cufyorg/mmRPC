package org.cufy.mmrpc.runtime.kafka.internal

import org.apache.kafka.clients.producer.KafkaProducer
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.kafka.KafkaClientEngine
import org.cufy.mmrpc.runtime.kafka.util.KafkaClientNegotiator

@OptIn(ExperimentalMmrpcApi::class)
internal fun createKafkaMmrpcClient(
    producer: KafkaProducer<*, *>,
    block: KafkaClientEngine.Builder.() -> Unit = {},
): KafkaClientEngine {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : KafkaClientEngine.Builder {
        val interceptors = mutableListOf<Interceptor.Client>()
        var negotiator: KafkaClientNegotiator? = null

        override fun install(interceptor: Interceptor.Client) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: KafkaClientNegotiator) {
            check(this.negotiator == null) { "Negotiator already installed" }
            this.negotiator = negotiator
        }
    }

    builder.apply(block)

    val engine = KafkaClientEngine(
        producer = producer,
        negotiator = builder.negotiator
            ?: KafkaClientNegotiator.Default,
        interceptors = builder.interceptors,
    )

    return engine
}
