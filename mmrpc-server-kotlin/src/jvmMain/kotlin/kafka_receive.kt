package org.cufy.mmrpc.server

import org.cufy.kaftor.KafkaEvent
import org.cufy.kaftor.header
import org.cufy.kaftor.receiveText
import org.cufy.mmrpc.RoutineObject
import kotlin.Result.Companion.failure

suspend fun <I : Any> KafkaEvent.receiveCatching(routine: RoutineObject<I, *>): Result<I> {
    val contentType = record.header("Content-Type")

    return when (contentType ?: "application/json") {
        "application/json" -> receiveText().deserializeJsonCatchingUnsafe(routine.typeI, json)
        else -> failure(IllegalArgumentException("Unsupported Content Type: $contentType"))
    }
}

suspend fun <I : Any> KafkaEvent.receive(routine: RoutineObject<I, *>): I {
    return receiveCatching(routine).getOrThrow()
}
