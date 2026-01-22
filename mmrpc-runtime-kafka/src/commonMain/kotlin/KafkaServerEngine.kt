package org.cufy.mmrpc.runtime.kafka

import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.commit
import org.cufy.kaftor.consume
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldRequest
import org.cufy.mmrpc.runtime.ServerEngine
import org.cufy.mmrpc.runtime.kafka.util.KafkaServerNegotiator

@OptIn(ExperimentalMmrpcApi::class)
class KafkaServerEngine @ExperimentalMmrpcApi constructor(
    val route: KafkaRoute,
    val negotiator: KafkaServerNegotiator,
    val interceptors: List<Interceptor.Server>,
) : ServerEngine.Kafka {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: Interceptor.Server)
        @ExperimentalMmrpcApi
        fun install(negotiator: KafkaServerNegotiator)
        fun routing(block: context(KafkaServerEngine) () -> Unit)
    }

    override fun is0Supported() = true

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        route.consume(canonicalName) {
            val ctx = KafkaServerContext(event, canonicalName)
            withContext(ctx) {
                val request = with(ctx) { negotiator.getRequest(reqSerial) }
                val foldReq = foldRequest(interceptors, canonicalName, request)
                handler(foldReq)
                event.commit()
            }
        }
    }
}
