package org.cufy.mmrpc.runtime.grpc.util

import kotlinx.coroutines.flow.Flow
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.grpc.GrpcClientContext
import org.cufy.mmrpc.runtime.grpc.grpcClientContext

abstract class GrpcClientInterceptor : Interceptor.Client {
    @ExperimentalMmrpcApi
    context(_: GrpcClientContext)
    open suspend fun <Req> onRequest(canonicalName: String, request: Req): Req = request

    @ExperimentalMmrpcApi
    context(_: GrpcClientContext)
    open suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> = stream

    @ExperimentalMmrpcApi
    context(_: GrpcClientContext)
    open suspend fun <Res> onResponse(canonicalName: String, response: Res): Res = response

    @ExperimentalMmrpcApi
    context(_: GrpcClientContext)
    open suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> = stream

    @ExperimentalMmrpcApi
    context(_: GrpcClientContext)
    open suspend fun onError(canonicalName: String, error: FaultObject): FaultObject = error

    ////////////////////////////////////////

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, request: Req): Req =
        context(grpcClientContext()) { onRequest(canonicalName, request) }

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> =
        context(grpcClientContext()) { onRequest(canonicalName, stream) }

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, response: Res): Res =
        context(grpcClientContext()) { onResponse(canonicalName, response) }

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> =
        context(grpcClientContext()) { onResponse(canonicalName, stream) }

    @ExperimentalMmrpcApi
    final override suspend fun onError(canonicalName: String, error: FaultObject): FaultObject =
        context(grpcClientContext()) { onError(canonicalName, error) }
}
