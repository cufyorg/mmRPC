package org.cufy.mmrpc.runtime.grpc.util

import kotlinx.coroutines.flow.Flow
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.grpc.GrpcServerContext
import org.cufy.mmrpc.runtime.grpc.grpcServerContext

abstract class GrpcServerInterceptor : Interceptor.Server {
    @ExperimentalMmrpcApi
    context(_: GrpcServerContext)
    open suspend fun <Req> onRequest(canonicalName: String, request: Req): Req = request

    @ExperimentalMmrpcApi
    context(_: GrpcServerContext)
    open suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> = stream

    @ExperimentalMmrpcApi
    context(_: GrpcServerContext)
    open suspend fun <Res> onResponse(canonicalName: String, response: Res): Res = response

    @ExperimentalMmrpcApi
    context(_: GrpcServerContext)
    open suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> = stream

    @ExperimentalMmrpcApi
    context(_: GrpcServerContext)
    open suspend fun onError(canonicalName: String, error: FaultObject): FaultObject = error

    ////////////////////////////////////////

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, request: Req): Req =
        context(grpcServerContext()) { onRequest(canonicalName, request) }

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> =
        context(grpcServerContext()) { onRequest(canonicalName, stream) }

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, response: Res): Res =
        context(grpcServerContext()) { onResponse(canonicalName, response) }

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> =
        context(grpcServerContext()) { onResponse(canonicalName, stream) }

    @ExperimentalMmrpcApi
    final override suspend fun onError(canonicalName: String, error: FaultObject): FaultObject =
        context(grpcServerContext()) { onError(canonicalName, error) }
}
