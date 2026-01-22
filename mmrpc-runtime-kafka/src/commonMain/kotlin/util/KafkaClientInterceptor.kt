package org.cufy.mmrpc.runtime.kafka.util

import kotlinx.coroutines.flow.Flow
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.kafka.KafkaClientContext
import org.cufy.mmrpc.runtime.kafka.kafkaClientContext

abstract class KafkaClientInterceptor : Interceptor.Server {
    @ExperimentalMmrpcApi
    context(_: KafkaClientContext)
    open suspend fun <Req> onRequest(canonicalName: String, request: Req): Req = request

    ////////////////////////////////////////

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, request: Req): Req =
        context(kafkaClientContext()) { onRequest(canonicalName, request) }

    @ExperimentalMmrpcApi
    final override suspend fun <Req> onRequest(canonicalName: String, stream: Flow<Req>): Flow<Req> =
        error("KafkaClientInterceptor.onRequest(String, Flow<Req>) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, response: Res): Res =
        error("KafkaClientInterceptor.onResponse(String, Res) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun <Res> onResponse(canonicalName: String, stream: Flow<Res>): Flow<Res> =
        error("KafkaClientInterceptor.onResponse(String, Flow<Res>) is not supported")

    @ExperimentalMmrpcApi
    final override suspend fun onError(canonicalName: String, error: FaultObject): FaultObject =
        error("KafkaClientInterceptor.onError(String, FaultObject) is not supported")
}
