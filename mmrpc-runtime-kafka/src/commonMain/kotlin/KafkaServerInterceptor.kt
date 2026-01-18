package org.cufy.mmrpc.runtime.kafka

import org.cufy.kaftor.KafkaEvent

interface KafkaServerInterceptor {
    /**
     * Called before handling the request.
     *
     * @return false, to prevent handling the request.
     */
    context(engine: KafkaServerEngine)
    suspend fun onReq(
        ctx: KafkaEvent,
        canonicalName: String,
        request: Any,
    ) = true
}
