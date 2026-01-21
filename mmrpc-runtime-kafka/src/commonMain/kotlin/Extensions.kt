package org.cufy.mmrpc.runtime.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.utils.dsl.KaftorDsl
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi

@KaftorDsl
@OptIn(ExperimentalMmrpcApi::class)
context(route: KafkaRoute)
fun mmrpc(block: KafkaServerEngine.Builder.() -> Unit) {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : KafkaServerEngine.Builder {
        val interceptors = mutableListOf<KafkaServerInterceptor>()
        var contentNegotiator: KafkaServerContentNegotiator? = null
        val routes = mutableListOf<context(KafkaServerEngine) () -> Unit>()

        override fun install(interceptor: KafkaServerInterceptor) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: KafkaServerContentNegotiator) {
            check(contentNegotiator == null) { "Content negotiator already installed" }
            contentNegotiator = negotiator
        }

        override fun routing(block: context(KafkaServerEngine) () -> Unit) {
            routes.add(block)
        }
    }

    builder.apply(block)

    val engine = KafkaServerEngine(
        route = route,
        contentNegotiator = builder.contentNegotiator
            ?: KafkaServerContentNegotiator.Default,
        interceptors = builder.interceptors,
    )

    builder.routes.forEach {
        engine.apply(it)
    }
}

@OptIn(ExperimentalMmrpcApi::class)
fun KafkaProducer<*, *>.mmrpc(block: KafkaClientEngine.Builder.() -> Unit = {}): KafkaClientEngine {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : KafkaClientEngine.Builder {
        val interceptors = mutableListOf<KafkaClientInterceptor>()
        var contentNegotiator: KafkaClientContentNegotiator? = null

        override fun install(interceptor: KafkaClientInterceptor) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: KafkaClientContentNegotiator) {
            check(contentNegotiator == null) { "Content negotiator already installed" }
            contentNegotiator = negotiator
        }
    }

    builder.apply(block)

    val engine = KafkaClientEngine(
        producer = this,
        contentNegotiator = builder.contentNegotiator
            ?: KafkaClientContentNegotiator.Default,
        interceptors = builder.interceptors,
    )

    return engine
}
