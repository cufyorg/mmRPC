package org.cufy.mmrpc.runtime.grpc

import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import io.grpc.kotlin.ClientCalls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FdxClientEngine
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldError
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldRequest
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldResponse
import org.cufy.mmrpc.runtime.grpc.internal.*
import org.cufy.mmrpc.runtime.toFaultException

@OptIn(ExperimentalMmrpcApi::class)
class GrpcClientEngine(
    val channel: ManagedChannel,
    val interceptors: List<Interceptor.Client>,
) : FdxClientEngine() {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: Interceptor.Client)
    }

    override suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    ) {
        val ctx = GrpcClientContext(canonicalName)
        withContext(ctx) {
            val foldReq = foldRequest(interceptors, canonicalName, request)
            ClientCalls.unaryRpc(
                channel = channel,
                method = descriptor0(canonicalName, reqSerial),
                request = foldReq,
                callOptions = ctx.request.options,
                headers = ctx.request.headers,
            )
        }
    }

    override suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res {
        val ctx = GrpcClientContext(canonicalName)
        return withContext(ctx) {
            val response = try {
                val foldReq = foldRequest(interceptors, canonicalName, request)
                ClientCalls.unaryRpc(
                    channel = channel,
                    method = descriptor1(canonicalName, reqSerial, resSerial),
                    request = foldReq,
                    callOptions = ctx.request.options,
                    headers = ctx.request.headers,
                )
            } catch (cause: StatusRuntimeException) {
                val error = cause.toFaultObjectOrNull() ?: throw cause
                val foldErr = foldError(interceptors, canonicalName, error)
                throw foldErr.toFaultException(cause)
            }

            val foldRes = foldResponse(interceptors, canonicalName, response)
            foldRes
        }
    }

    override suspend fun <Req : Any, Res : Any> exec2(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>
    ): Res {
        val ctx = GrpcClientContext(canonicalName)
        return withContext(ctx) {
            val response = try {
                val foldReq = foldRequest(interceptors, canonicalName, request)
                ClientCalls.clientStreamingRpc(
                    channel = channel,
                    method = descriptor2(canonicalName, reqSerial, resSerial),
                    requests = foldReq,
                    callOptions = ctx.request.options,
                    headers = ctx.request.headers,
                )
            } catch (cause: StatusRuntimeException) {
                val error = cause.toFaultObjectOrNull() ?: throw cause
                val foldErr = foldError(interceptors, canonicalName, error)
                throw foldErr.toFaultException(cause)
            }

            val foldRes = foldResponse(interceptors, canonicalName, response)
            foldRes
        }
    }

    override fun <Req : Any, Res : Any> exec3(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>
    ): Flow<Res> {
        return flow {
            val ctx = GrpcClientContext(canonicalName)
            withContext(ctx) {
                val response = try {
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    ClientCalls.serverStreamingRpc(
                        channel = channel,
                        method = descriptor3(canonicalName, reqSerial, resSerial),
                        request = foldReq,
                        callOptions = ctx.request.options,
                        headers = ctx.request.headers,
                    )
                } catch (cause: StatusRuntimeException) {
                    val error = cause.toFaultObjectOrNull() ?: throw cause
                    val foldErr = foldError(interceptors, canonicalName, error)
                    throw foldErr.toFaultException(cause)
                }

                val foldRes = foldResponse(interceptors, canonicalName, response)
                emitAll(foldRes)
            }
        }
    }

    override fun <Req : Any, Res : Any> exec4(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>
    ): Flow<Res> {
        return flow {
            val ctx = GrpcClientContext(canonicalName)
            withContext(ctx) {
                val response = try {
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    ClientCalls.bidiStreamingRpc(
                        channel = channel,
                        method = descriptor4(canonicalName, reqSerial, resSerial),
                        requests = foldReq,
                        callOptions = ctx.request.options,
                        headers = ctx.request.headers,
                    )
                } catch (cause: StatusRuntimeException) {
                    val error = cause.toFaultObjectOrNull() ?: throw cause
                    val foldErr = foldError(interceptors, canonicalName, error)
                    throw foldErr.toFaultException(cause)
                }

                val foldRes = foldResponse(interceptors, canonicalName, response)
                emitAll(foldRes)
            }
        }
    }
}
