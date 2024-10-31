package org.cufy.mmrpc.server

import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.KafkaRoutingContext
import org.cufy.kaftor.consume
import org.cufy.kaftor.utils.dsl.KaftorDsl
import org.cufy.mmrpc.KafkaEndpointInfo
import org.cufy.mmrpc.KafkaPublicationEndpointInfo
import org.cufy.mmrpc.RoutineObject

@KaftorDsl
fun <R : RoutineObject<*, *>> KafkaRoute.handle(
    routine: R,
    block: suspend KafkaRoutingContext.(R) -> Unit,
) {
    val endpoints = routine.__info__.endpoints
        .filterIsInstance<KafkaEndpointInfo>()

    require(endpoints.isNotEmpty()) {
        "Routine does not have an Kafka endpoint"
    }

    for (endpoint in endpoints) {
        consume(endpoint.topic.value) {
            block(this, routine)
        }
    }
}

@KaftorDsl
fun <R : RoutineObject<*, *>> KafkaRoute.handlePublication(
    routine: R,
    block: suspend KafkaRoutingContext.(R) -> Unit,
) {
    val endpoints = routine.__info__.endpoints
        .filterIsInstance<KafkaPublicationEndpointInfo>()

    require(endpoints.isNotEmpty()) {
        "Routine does not have an KafkaPublication endpoint"
    }

    for (endpoint in endpoints) {
        consume(endpoint.topic.value) {
            block(this, routine)
        }
    }
}
