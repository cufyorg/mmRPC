package org.cufy.mmrpc.runtime.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.cufy.kaftor.KafkaEvent
import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.utils.dsl.KaftorDsl
import org.cufy.mmrpc.runtime.ClientContext
import org.cufy.mmrpc.runtime.ServerContext
import org.cufy.mmrpc.runtime.clientContext
import org.cufy.mmrpc.runtime.kafka.internal.applyKafkaMmrpcServer
import org.cufy.mmrpc.runtime.kafka.internal.createKafkaMmrpcClient
import org.cufy.mmrpc.runtime.serverContext

////////////////////////////////////////

class KafkaServerContext(
    val event: KafkaEvent,
    override val canonicalName: String,
) : ServerContext()

class KafkaClientContext(
    override val canonicalName: String,
) : ClientContext() {
    val request = Request()

    class Request {
        var partition: Int? = null
        var timestamp: Long? = null
        var key: Any? = null
        var value: Any? = null
        val headers = mutableMapOf<String, ByteArray>()
    }
}

suspend fun kafkaServerContext(): KafkaServerContext {
    return serverContext() as? KafkaServerContext
        ?: error("Kafka server context is not available")
}

suspend fun kafkaClientContext(): KafkaClientContext {
    return clientContext() as? KafkaClientContext
        ?: error("Kafka client context is not available")
}

////////////////////////////////////////

@KaftorDsl
context(route: KafkaRoute)
fun mmrpc(
    block: KafkaServerEngine.Builder.() -> Unit,
) {
    applyKafkaMmrpcServer(route, block)
}

fun KafkaProducer<*, *>.mmrpc(
    block: KafkaClientEngine.Builder.() -> Unit = {},
): KafkaClientEngine {
    return createKafkaMmrpcClient(this, block)
}

////////////////////////////////////////
