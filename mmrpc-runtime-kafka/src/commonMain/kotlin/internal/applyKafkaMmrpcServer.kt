package org.cufy.mmrpc.runtime.kafka.internal

import org.cufy.kaftor.KafkaRoute
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.kafka.KafkaServerEngine
import org.cufy.mmrpc.runtime.kafka.util.KafkaServerNegotiator

@OptIn(ExperimentalMmrpcApi::class)
internal fun applyKafkaMmrpcServer(
    route: KafkaRoute,
    block: KafkaServerEngine.Builder.() -> Unit,
) {
    // Just a reminder: engines in mmrpc are just thin wrappers

    val builder = object : KafkaServerEngine.Builder {
        val interceptors = mutableListOf<Interceptor.Server>()
        var negotiator: KafkaServerNegotiator? = null
        val routes = mutableListOf<context(KafkaServerEngine) () -> Unit>()

        override fun install(interceptor: Interceptor.Server) {
            interceptors.add(interceptor)
        }

        override fun install(negotiator: KafkaServerNegotiator) {
            check(this.negotiator == null) { "Negotiator already installed" }
            this.negotiator = negotiator
        }

        override fun routing(block: context(KafkaServerEngine) () -> Unit) {
            routes.add(block)
        }
    }

    builder.apply(block)

    val engine = KafkaServerEngine(
        route = route,
        negotiator = builder.negotiator
            ?: KafkaServerNegotiator.Default,
        interceptors = builder.interceptors,
    )

    builder.routes.forEach {
        engine.apply(it)
    }
}
