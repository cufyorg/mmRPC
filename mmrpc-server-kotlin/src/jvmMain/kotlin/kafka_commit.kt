package org.cufy.mmrpc.server

import org.cufy.kaftor.KafkaEvent
import org.cufy.kaftor.commit
import org.cufy.mmrpc.RoutineObject

suspend inline fun <reified O : Any> KafkaEvent.commit(
    routine: RoutineObject<*, O>,
    output: O,
) {
    commit()
}
