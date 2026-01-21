package org.cufy.mmrpc.runtime.kafka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi

@OptIn(ExperimentalMmrpcApi::class)
class KafkaClientEngine @ExperimentalMmrpcApi constructor(
    val producer: KafkaProducer<*, *>,
    val contentNegotiator: KafkaClientContentNegotiator,
    val interceptors: List<KafkaClientInterceptor>,
) : ClientEngine.Kafka {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: KafkaClientInterceptor)
        @ExperimentalMmrpcApi
        fun install(negotiator: KafkaClientContentNegotiator)
    }

    override fun is0Supported() = true

    override suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    ) {
        val record = KafkaRequestBuilder()
            .apply {
                topic = canonicalName
                contentNegotiator.setReq(this, reqSerial, request)
                interceptors.forEach { it.onReq(this, canonicalName, request) }
            }
            .build()

        withContext(Dispatchers.IO) {
            @Suppress("UNCHECKED_CAST")
            producer as KafkaProducer<Any?, Any?>
            @Suppress("UNCHECKED_CAST")
            record as ProducerRecord<Any?, Any?>
            producer.send(record)
        }
    }
}
