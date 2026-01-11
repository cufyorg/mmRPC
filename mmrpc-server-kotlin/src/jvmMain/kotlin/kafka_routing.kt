package org.cufy.mmrpc.server

import org.cufy.kaftor.KafkaRoute
import org.cufy.kaftor.KafkaRoutingContext
import org.cufy.kaftor.consume
import org.cufy.kaftor.utils.dsl.KaftorDsl
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.isKafkaSupported

@KaftorDsl
fun <R : RoutineObject<*, *>> KafkaRoute.handle(
    routine: R,
    block: suspend KafkaRoutingContext.(R) -> Unit,
) {
    require(routine.comm.isKafkaSupported()) {
        "Routine does not support Kafka communication channel"
    }

    consume(routine.canonicalName.value) {
        block(this, routine)
    }
}
