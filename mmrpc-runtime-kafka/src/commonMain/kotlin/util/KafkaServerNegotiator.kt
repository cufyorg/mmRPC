package org.cufy.mmrpc.runtime.kafka.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.kaftor.header
import org.cufy.kaftor.receiveText
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.kafka.KafkaServerContext

abstract class KafkaServerNegotiator {
    @ExperimentalMmrpcApi
    context(_: KafkaServerContext)
    abstract suspend fun <Req> getRequest(reqSerial: KSerializer<Req>): Req

    object Default : KafkaServerNegotiator() {
        @OptIn(ExperimentalMmrpcApi::class)
        context(ctx: KafkaServerContext)
        override suspend fun <Req> getRequest(reqSerial: KSerializer<Req>): Req {
            return when (val type = ctx.event.record.header("Content-Type")) {
                "application/json" -> {
                    val reqStr = ctx.event.receiveText()
                    Json.decodeFromString(reqSerial, reqStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }
    }
}
