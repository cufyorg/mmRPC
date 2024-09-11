package org.cufy.mmrpc.server

import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.json.buildJsonObject
import org.cufy.json.LenientJson
import org.cufy.json.decodeJsonCatching
import org.cufy.json.deserializeCatching
import org.cufy.json.deserializeJsonCatching
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject
import kotlin.Result.Companion.failure

inline fun <reified I : StructObject> ApplicationCall.receiveQueryCatching(): Result<I> {
    val element = buildJsonObject {
        for ((name, strings) in parameters.entries()) {
            val value = strings
                .joinToString("")
                .decodeJsonCatching(LenientJson)
                .getOrElse { return failure(it) }

            put(name, value)
        }
    }

    return element.deserializeCatching<I>(LenientJson)
}

suspend inline fun <reified I : StructObject> ApplicationCall.receiveJsonCatching(): Result<I> {
    return receiveText().deserializeJsonCatching<I>(LenientJson)
}

suspend inline fun <reified I : StructObject> ApplicationCall.receiveCatching(routine: RoutineObject<I, *>): Result<I> {
    return when (request.httpMethod) {
        Get, Delete -> receiveQueryCatching<I>()
        else -> receiveJsonCatching<I>()
    }
}

suspend inline fun <reified I : StructObject> ApplicationCall.receive(routine: RoutineObject<I, *>): I {
    return receiveCatching(routine).getOrThrow()
}
