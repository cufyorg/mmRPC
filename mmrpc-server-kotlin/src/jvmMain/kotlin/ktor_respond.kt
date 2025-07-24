package org.cufy.mmrpc.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*
import org.cufy.json.JsonObject
import org.cufy.json.set
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.FaultException
import org.cufy.mmrpc.FaultObject
import org.cufy.mmrpc.RoutineObject
import kotlin.reflect.jvm.jvmErasure

suspend fun <O : Any> ApplicationCall.respond(
    routine: RoutineObject<*, O>,
    output: O,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    response.status(status)
    respond(output, TypeInfo(routine.typeO.jvmErasure, routine.typeO))
}

suspend inline fun ApplicationCall.respond(
    routine: RoutineObject<*, *>,
    fault: FaultException,
    message: String? = fault.message,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    respond(status, JsonObject {
        this["type"] = fault.canonicalName.value
        this["message"] = message
    })
}

suspend inline fun ApplicationCall.respond(
    routine: RoutineObject<*, *>,
    fault: FaultObject,
    message: String? = null,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    respond(status, JsonObject {
        this["type"] = fault.canonicalName.value
        this["message"] = message
    })
}

suspend inline fun ApplicationCall.respond(
    routine: RoutineObject<*, *>,
    fault: CanonicalName,
    message: String? = null,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    respond(status, JsonObject {
        this["type"] = fault.value
        this["message"] = message
    })
}
