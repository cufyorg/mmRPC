package org.cufy.mmrpc.runtime.kafka

import kotlinx.serialization.KSerializer
import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.commit
import org.cufy.kaftor.consume
import org.cufy.mmrpc.runtime.ServerEngine

class KafkaServerEngine(
    val route: KafkaRoute,
    val contentNegotiator: KafkaServerContentNegotiator =
        KafkaServerContentNegotiator.Default,
) : ServerEngine.Kafka {
    override fun is0Supported() = true

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        route.consume(canonicalName) {
            val request = contentNegotiator.getReq(event, reqSerial)
            handler(request)
            event.commit()
        }
    }
}
