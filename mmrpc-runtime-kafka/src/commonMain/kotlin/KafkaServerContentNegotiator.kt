package org.cufy.mmrpc.runtime.kafka

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.kaftor.KafkaEvent
import org.cufy.kaftor.header
import org.cufy.kaftor.receiveText
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi

interface KafkaServerContentNegotiator {
    @ExperimentalMmrpcApi
    suspend fun <Req> getReq(ctx: KafkaEvent, reqSerial: KSerializer<Req>): Req

    object Default : KafkaServerContentNegotiator {
        @OptIn(ExperimentalMmrpcApi::class)
        override suspend fun <Req> getReq(ctx: KafkaEvent, reqSerial: KSerializer<Req>): Req {
            return when (val type = ctx.record.header("Content-Type")) {
                "application/json" -> {
                    val reqStr = ctx.receiveText()
                    Json.decodeFromString(reqSerial, reqStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }
    }
}
