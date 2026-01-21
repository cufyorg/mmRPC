package org.cufy.mmrpc.runtime.kafka

import org.cufy.kaftor.KafkaEvent
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi

interface KafkaServerInterceptor {
    /**
     * Called before handling the request.
     *
     * @return false, to prevent handling the request.
     */
    @ExperimentalMmrpcApi
    context(engine: KafkaServerEngine)
    suspend fun onReq(
        ctx: KafkaEvent,
        canonicalName: String,
        request: Any,
    ) = true
}
