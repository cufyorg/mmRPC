package org.cufy.mmrpc.runtime.http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject

interface HttpClientInterceptor {
    /**
     * Called before sending the request.
     */
    @ExperimentalMmrpcApi
    context(engine: HttpClientEngine)
    suspend fun onReq(
        ctx: HttpRequestBuilder,
        canonicalName: String,
        request: Any,
    ) {
    }

    /**
     * Called after sending the request and before returning the response.
     */
    @ExperimentalMmrpcApi
    context(engine: HttpClientEngine)
    suspend fun onRes(
        ctx: HttpResponse,
        canonicalName: String,
        request: Any,
        response: Any,
    ) {
    }

    /**
     * Called after sending the request and before throwing the fault.
     */
    @ExperimentalMmrpcApi
    context(engine: HttpClientEngine)
    suspend fun onErr(
        ctx: HttpResponse,
        canonicalName: String,
        request: Any,
        error: FaultObject,
    ) {
    }
}
