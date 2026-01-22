package org.cufy.mmrpc.runtime.http.util

import kotlinx.coroutines.flow.Flow
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.http.HttpServerContext
import org.cufy.mmrpc.runtime.http.httpServerContext

abstract class HttpServerInterceptor : Interceptor.Server {
    @ExperimentalMmrpcApi
    context(_: HttpServerContext)
    open suspend fun <Req> onRequest(canonicalName: String, request: Req): Req = request

    @ExperimentalMmrpcApi
    context(_: HttpServerContext)
    open suspend fun <Res> onResponse(canonicalName: String, response: Res): Res = response

    @ExperimentalMmrpcApi
    context(_: HttpServerContext)
    open suspend fun onError(canonicalName: String, error: FaultObject): FaultObject = error

    ////////////////////////////////////////

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, request: Req): Req =
        context(httpServerContext()) { onRequest(canonicalName, request) }

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> =
        error("HttpServerInterceptor.onRequest(String, Flow<Req>) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, response: Res): Res =
        context(httpServerContext()) { onResponse(canonicalName, response) }

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> =
        error("HttpServerInterceptor.onResponse(String, Flow<Res>) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun onError(canonicalName: String, error: FaultObject): FaultObject =
        context(httpServerContext()) { onError(canonicalName, error) }
}
