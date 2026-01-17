package org.cufy.mmrpc.runtime.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.cufy.kaftor.KafkaRoute

context(route: KafkaRoute)
fun mmRPC(block: KafkaServerEngine.() -> Unit) {
    KafkaServerEngine(route).block()
}

fun KafkaProducer<*, *>.mmRPC(): KafkaClientEngine {
    return KafkaClientEngine(this)
}
