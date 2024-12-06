package org.cufy.mmrpc.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.cufy.json.JsonObject
import org.cufy.json.set
import org.cufy.mmrpc.*

suspend inline fun <reified O : StructObject> ApplicationCall.respond(
    routine: RoutineObject<*, O>,
    output: O,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    respond(status, output)
}

suspend inline fun ApplicationCall.respond(
    fault: FaultException,
    message: String? = fault.message,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    val json = JsonObject {
        this["type"] = fault.canonicalName.value
        this["message"] = message
    }

    respond(status, json)
}

suspend inline fun ApplicationCall.respond(
    fault: FaultObject,
    message: String? = null,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    val json = JsonObject {
        this["type"] = fault.__info__.canonicalName.value
        this["message"] = message
    }

    respond(status, json)
}

suspend inline fun ApplicationCall.respond(
    fault: FaultInfo,
    message: String? = null,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    val json = JsonObject {
        this["type"] = fault.canonicalName.value
        this["message"] = message
    }

    respond(status, json)
}
