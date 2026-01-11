package org.cufy.mmrpc.server

import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.isHttpSupported

@KtorDsl
fun <R : RoutineObject<*, *>> Route.handle(
    routine: R,
    block: suspend RoutingContext.(R) -> Unit,
) {
    require(routine.comm.isHttpSupported()) {
        "Routine does not support Http communication channel"
    }

    post(routine.canonicalName.value) {
        block(this, routine)
    }
}
