package org.cufy.mmrpc.runtime.http

import io.ktor.server.application.*
import org.cufy.mmrpc.runtime.FaultObject

interface HttpServerInterceptor {
    /**
     * Called before handling the request.
     *
     * @return false, to prevent handling the request.
     */
    context(engine: HttpServerEngine)
    fun onReq(
        ctx: ApplicationCall,
        canonicalName: String,
        request: Any,
    ) = true

    /**
     * Called after handling the request and before sending the response.
     *
     * @return false, to prevent sending the response.
     */
    context(engine: HttpServerEngine)
    fun onRes(
        ctx: ApplicationCall,
        canonicalName: String,
        request: Any,
        response: Any,
    ) = true

    /**
     * Called when a fault is thrown and before sending the fault.
     *
     * @return false, to prevent sending the fault.
     */
    context(engine: HttpServerEngine)
    fun onErr(
        ctx: ApplicationCall,
        canonicalName: String,
        request: Any,
        error: FaultObject,
    ) = true
}
