package org.cufy.mmrpc.server

import org.cufy.jose.decodeCompactJWSCatching
import org.cufy.jose.unverified
import org.cufy.json.LenientJson
import org.cufy.json.deserializeJsonCatching
import org.cufy.kafka.routing.KafkaEvent
import org.cufy.kafka.routing.header
import org.cufy.kafka.routing.receiveText
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject
import kotlin.Result.Companion.failure

suspend inline fun <reified I : StructObject> KafkaEvent.receiveJWTCatching(): Result<I> {
    return receiveText()
        .decodeCompactJWSCatching()
        .getOrElse { return failure(it) }
        .unverified()
        .payload
        .deserializeJsonCatching<I>()
}

suspend inline fun <reified I : StructObject> KafkaEvent.receiveJsonCatching(): Result<I> {
    return receiveText().deserializeJsonCatching<I>(LenientJson)
}

suspend inline fun <reified I : StructObject> KafkaEvent.receiveCatching(routine: RoutineObject<I, *>): Result<I> {
    val contentType = record.header("Content-Type")

    return when (contentType ?: "application/json") {
        "application/json" -> receiveJsonCatching<I>()
        "application/jwt" -> receiveJWTCatching<I>()
        else -> failure(IllegalArgumentException("Unsupported Content Type: $contentType"))
    }
}

suspend inline fun <reified I : StructObject> KafkaEvent.receive(routine: RoutineObject<I, *>): I {
    return receiveCatching(routine).getOrThrow()
}
