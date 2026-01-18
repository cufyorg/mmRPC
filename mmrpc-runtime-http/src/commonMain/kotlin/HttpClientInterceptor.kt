package org.cufy.mmrpc.runtime.http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.cufy.mmrpc.runtime.FaultObject

interface HttpClientInterceptor {
    /**
     * Called before sending the request.
     */
    context(engine: HttpClientEngine)
    fun onReq(
        ctx: HttpRequestBuilder,
        canonicalName: String,
        request: Any,
    )

    /**
     * Called after sending the request and before returning the response.
     */
    context(engine: HttpClientEngine)
    fun onRes(
        ctx: HttpResponse,
        canonicalName: String,
        request: Any,
        response: Any,
    )

    /**
     * Called after sending the request and before throwing the fault.
     */
    context(engine: HttpClientEngine)
    fun onErr(
        ctx: HttpResponse,
        canonicalName: String,
        request: Any,
        error: FaultObject,
    )
}
