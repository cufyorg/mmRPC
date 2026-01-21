package org.cufy.mmrpc.runtime.kafka

import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi

interface KafkaClientInterceptor {
    /**
     * Called before sending the request.
     */
    @ExperimentalMmrpcApi
    context(engine: KafkaClientEngine)
    suspend fun onReq(
        ctx: KafkaRequestBuilder,
        canonicalName: String,
        request: Any,
    ) {
    }
}
