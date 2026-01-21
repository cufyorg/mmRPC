package org.cufy.mmrpc.runtime.kafka

import kotlinx.serialization.KSerializer
import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.commit
import org.cufy.kaftor.consume
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.ServerEngine

@OptIn(ExperimentalMmrpcApi::class)
class KafkaServerEngine @ExperimentalMmrpcApi constructor(
    val route: KafkaRoute,
    val contentNegotiator: KafkaServerContentNegotiator,
    val interceptors: List<KafkaServerInterceptor>,
) : ServerEngine.Kafka {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: KafkaServerInterceptor)
        @ExperimentalMmrpcApi
        fun install(negotiator: KafkaServerContentNegotiator)
        fun routing(block: context(KafkaServerEngine) () -> Unit)
    }

    override fun is0Supported() = true

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        route.consume(canonicalName) {
            val request = contentNegotiator.getReq(event, reqSerial)

            if (!interceptors.all { it.onReq(event, canonicalName, request) })
                return@consume

            handler(request)
            event.commit()
        }
    }
}
