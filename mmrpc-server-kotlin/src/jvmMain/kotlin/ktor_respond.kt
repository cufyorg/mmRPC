package org.cufy.mmrpc.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.cufy.json.JsonObject
import org.cufy.json.set
import org.cufy.mmrpc.FaultInfo
import org.cufy.mmrpc.FaultObject
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject

suspend inline fun <reified O : StructObject> ApplicationCall.respond(
    routine: RoutineObject<*, O>,
    output: O,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    respond(status, output)
}

suspend inline fun ApplicationCall.respond(
    fault: FaultObject,
    message: String? = null,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    respond(fault.__info__, message, status)
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
