package org.cufy.mmrpc.runtime.grpc

import com.google.protobuf.Empty
import io.grpc.ServerMethodDefinition
import io.grpc.kotlin.ServerCalls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.*
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldError
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldRequest
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldResponse
import org.cufy.mmrpc.runtime.grpc.internal.*
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMmrpcApi::class)
class GrpcServerEngine @ExperimentalMmrpcApi constructor(
    val coroutineContext: CoroutineContext,
    val interceptors: List<Interceptor.Server>,
    private val register: (ServerMethodDefinition<*, *>) -> Unit,
) : FdxServerEngine() {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: Interceptor.Server)
        fun routing(block: context(GrpcServerEngine) () -> Unit)
    }

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        register(ServerCalls.unaryServerMethodDefinition(
            context = coroutineContext,
            descriptor = descriptor0(canonicalName, reqSerial),
            implementation = { request ->
                // btw: context is injected via grpc interceptor
                val foldReq = foldRequest(interceptors, canonicalName, request)
                handler(foldReq)
                Empty.getDefaultInstance()
            }
        ))
    }

    override fun <Req : Any, Res : Any> register1(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Req) -> Res
    ) {
        register(ServerCalls.unaryServerMethodDefinition(
            context = coroutineContext,
            descriptor = descriptor1(canonicalName, reqSerial, resSerial),
            implementation = { request ->
                // btw: context is injected via grpc interceptor
                try {
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    val response = handler(foldReq)
                    val foldRes = foldResponse(interceptors, canonicalName, response)
                    foldRes
                } catch (e: FaultException) {
                    val error = e.toFaultObject()
                    val foldErr = foldError(interceptors, canonicalName, error)
                    throw foldErr.toStatusRuntimeException()
                }
            }
        ))
    }

    override fun <Req : Any, Res : Any> register2(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Flow<Req>) -> Res
    ) {
        register(ServerCalls.clientStreamingServerMethodDefinition(
            context = coroutineContext,
            descriptor = descriptor2(canonicalName, reqSerial, resSerial),
            implementation = { request ->
                // btw: context is injected via grpc interceptor
                try {
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    val response = handler(foldReq)
                    val foldRes = foldResponse(interceptors, canonicalName, response)
                    foldRes
                } catch (e: FaultException) {
                    val error = e.toFaultObject()
                    val foldErr = foldError(interceptors, canonicalName, error)
                    throw foldErr.toStatusRuntimeException()
                }
            }
        ))
    }

    override fun <Req : Any, Res : Any> register3(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: (Req) -> Flow<Res>
    ) {
        register(ServerCalls.serverStreamingServerMethodDefinition(
            context = coroutineContext,
            descriptor = descriptor3(canonicalName, reqSerial, resSerial),
            implementation = { request ->
                // btw: context is injected via grpc interceptor
                flow {
                    try {
                        val foldReq = foldRequest(interceptors, canonicalName, request)
                        val response = handler(foldReq)
                        val foldRes = foldResponse(interceptors, canonicalName, response)
                        emitAll(foldRes)
                    } catch (e: FaultException) {
                        val error = e.toFaultObject()
                        val foldErr = foldError(interceptors, canonicalName, error)
                        throw foldErr.toStatusRuntimeException()
                    }
                }
            }
        ))
    }

    override fun <Req : Any, Res : Any> register4(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: (Flow<Req>) -> Flow<Res>
    ) {
        register(ServerCalls.bidiStreamingServerMethodDefinition(
            context = coroutineContext,
            descriptor = descriptor4(canonicalName, reqSerial, resSerial),
            implementation = { request ->
                // btw: context is injected via grpc interceptor
                flow {
                    try {
                        val foldReq = foldRequest(interceptors, canonicalName, request)
                        val response = handler(foldReq)
                        val foldRes = foldResponse(interceptors, canonicalName, response)
                        emitAll(foldRes)
                    } catch (e: FaultException) {
                        val error = e.toFaultObject()
                        val foldErr = foldError(interceptors, canonicalName, error)
                        throw foldErr.toStatusRuntimeException()
                    }
                }
            }
        ))
    }
}
