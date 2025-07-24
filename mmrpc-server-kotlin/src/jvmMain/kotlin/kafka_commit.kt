package org.cufy.mmrpc.server

import org.cufy.kaftor.KafkaEvent
import org.cufy.kaftor.commit
import org.cufy.mmrpc.RoutineObject

suspend fun <O : Any> KafkaEvent.commit(
    routine: RoutineObject<*, O>,
    output: O,
) {
    commit()
}
