package org.cufy.mmrpc.runtime.kafka

import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldRequest
import org.cufy.mmrpc.runtime.kafka.internal.send
import org.cufy.mmrpc.runtime.kafka.util.KafkaClientNegotiator

@OptIn(ExperimentalMmrpcApi::class)
class KafkaClientEngine @ExperimentalMmrpcApi constructor(
    val producer: KafkaProducer<*, *>,
    val negotiator: KafkaClientNegotiator,
    val interceptors: List<Interceptor.Client>,
) : ClientEngine.Kafka {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: Interceptor.Client)
        @ExperimentalMmrpcApi
        fun install(negotiator: KafkaClientNegotiator)
    }

    override fun is0Supported() = true

    override suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    ) {
        val ctx = KafkaClientContext(canonicalName)
        withContext(ctx) {
            val foldReq = foldRequest(interceptors, canonicalName, request)
            with(ctx) { negotiator.setRequest(reqSerial, foldReq) }
            producer.send(
                topic = canonicalName,
                partition = ctx.request.partition,
                timestamp = ctx.request.timestamp,
                key = ctx.request.key,
                value = ctx.request.value,
                headers = ctx.request.headers,
            )
        }
    }
}
