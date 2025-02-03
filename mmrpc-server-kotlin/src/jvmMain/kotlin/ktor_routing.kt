package org.cufy.mmrpc.server

import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.RoutineObject

@KtorDsl
fun <R : RoutineObject<*, *>> Route.handle(
    routine: R,
    block: suspend RoutingContext.(R) -> Unit,
) {
    require(Comm.Http in routine.comm) {
        "Routine does not support Http communication channel"
    }

    post(routine.canonicalName.value) {
        block(this, routine)
    }
}
