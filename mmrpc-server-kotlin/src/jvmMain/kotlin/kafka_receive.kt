package org.cufy.mmrpc.server

import org.cufy.json.deserializeJsonCatching
import org.cufy.kaftor.KafkaEvent
import org.cufy.kaftor.header
import org.cufy.kaftor.receiveText
import org.cufy.mmrpc.RoutineObject
import kotlin.Result.Companion.failure

suspend inline fun <reified I : Any> KafkaEvent.receiveCatching(routine: RoutineObject<I, *>): Result<I> {
    val contentType = record.header("Content-Type")

    return when (contentType ?: "application/json") {
        "application/json" -> receiveText().deserializeJsonCatching<I>(json)
        else -> failure(IllegalArgumentException("Unsupported Content Type: $contentType"))
    }
}

suspend inline fun <reified I : Any> KafkaEvent.receive(routine: RoutineObject<I, *>): I {
    return receiveCatching(routine).getOrThrow()
}
