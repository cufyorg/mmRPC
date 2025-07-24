package org.cufy.mmrpc.server

import io.ktor.server.application.*
import io.ktor.server.request.*
import org.cufy.mmrpc.RoutineObject
import kotlin.Result.Companion.failure

suspend fun <I : Any> ApplicationCall.receiveCatching(routine: RoutineObject<I, *>): Result<I> {
    val contentType = request.header("Content-Type")

    return when (contentType ?: "application/json") {
        "application/json" -> receiveText().deserializeJsonCatchingUnsafe(routine.typeI, json)
        else -> failure(IllegalArgumentException("Unsupported Content Type: $contentType"))
    }
}

suspend fun <I : Any> ApplicationCall.receive(routine: RoutineObject<I, *>): I {
    return receiveCatching(routine).getOrThrow()
}
