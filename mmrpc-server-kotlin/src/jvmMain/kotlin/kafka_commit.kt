package org.cufy.mmrpc.server

import org.cufy.kafka.routing.KafkaEvent
import org.cufy.kafka.routing.commit
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject

suspend inline fun <reified O : StructObject> KafkaEvent.commit(
    routine: RoutineObject<*, O>,
    output: O,
) {
    commit()
}
