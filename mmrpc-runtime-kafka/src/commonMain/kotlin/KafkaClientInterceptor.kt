package org.cufy.mmrpc.runtime.kafka

interface KafkaClientInterceptor {
    /**
     * Called before sending the request.
     */
    context(engine: KafkaClientEngine)
    suspend fun onReq(
        ctx: KafkaRequestBuilder,
        canonicalName: String,
        request: Any,
    ) {
    }
}
