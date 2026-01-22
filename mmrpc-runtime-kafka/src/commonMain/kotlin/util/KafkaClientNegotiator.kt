package org.cufy.mmrpc.runtime.kafka.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.kafka.KafkaClientContext

abstract class KafkaClientNegotiator {
    @ExperimentalMmrpcApi
    context(_: KafkaClientContext)
    abstract suspend fun <Req> setRequest(reqSerial: KSerializer<Req>, request: Req)

    object Default : KafkaClientNegotiator() {
        @ExperimentalMmrpcApi
        context(ctx: KafkaClientContext)
        override suspend fun <Req> setRequest(reqSerial: KSerializer<Req>, request: Req) {
            val reqStr = Json.encodeToString(reqSerial, request)
            ctx.request.headers["Content-Type"] = "application/json".encodeToByteArray()
            ctx.request.value = reqStr
        }
    }
}
