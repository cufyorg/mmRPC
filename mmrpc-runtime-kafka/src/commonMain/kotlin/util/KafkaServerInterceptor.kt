package org.cufy.mmrpc.runtime.kafka.util

import kotlinx.coroutines.flow.Flow
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.kafka.KafkaServerContext
import org.cufy.mmrpc.runtime.kafka.kafkaServerContext

abstract class KafkaServerInterceptor : Interceptor.Server {
    @ExperimentalMmrpcApi
    context(_: KafkaServerContext)
    open suspend fun <Req> onRequest(canonicalName: String, request: Req): Req = request

    ////////////////////////////////////////

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, request: Req): Req =
        context(kafkaServerContext()) { onRequest(canonicalName, request) }

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> =
        error("KafkaServerInterceptor.onRequest(String, Flow<Req>) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, response: Res): Res =
        error("KafkaServerInterceptor.onResponse(String, Res) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> =
        error("KafkaServerInterceptor.onResponse(String, Flow<Res>) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun onError(canonicalName: String, error: FaultObject): FaultObject =
        error("KafkaServerInterceptor.onError(String, FaultObject) is not supported")
}
