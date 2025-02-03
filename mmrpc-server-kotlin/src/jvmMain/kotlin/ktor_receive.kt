package org.cufy.mmrpc.server

import io.ktor.server.application.*
import io.ktor.server.request.*
import org.cufy.json.deserializeJsonCatching
import org.cufy.mmrpc.RoutineObject
import kotlin.Result.Companion.failure

suspend inline fun <reified I : Any> ApplicationCall.receiveCatching(routine: RoutineObject<I, *>): Result<I> {
    val contentType = request.header("Content-Type")

    return when (contentType ?: "application/json") {
        "application/json" -> receiveText().deserializeJsonCatching<I>(json)
        else -> failure(IllegalArgumentException("Unsupported Content Type: $contentType"))
    }
}

suspend inline fun <reified I : Any> ApplicationCall.receive(routine: RoutineObject<I, *>): I {
    return receiveCatching(routine).getOrThrow()
}
