package org.cufy.mmrpc.runtime.kafka

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface KafkaClientContentNegotiator {
    suspend fun <Req> setReq(ctx: KafkaRequestBuilder, reqSerial: KSerializer<Req>, request: Req)

    object Default : KafkaClientContentNegotiator {
        override suspend fun <Req> setReq(
            ctx: KafkaRequestBuilder,
            reqSerial: KSerializer<Req>,
            request: Req
        ) {
            val reqStr = Json.encodeToString(reqSerial, request)
            ctx.headers["Content-Type"] = "application/json".encodeToByteArray()
            ctx.value = reqStr
        }
    }
}
