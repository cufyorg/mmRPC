package org.cufy.mmrpc.runtime.http

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.*
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldError
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldRequest
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldResponse
import org.cufy.mmrpc.runtime.http.util.HttpServerNegotiator

@OptIn(ExperimentalMmrpcApi::class)
class HttpServerEngine @ExperimentalMmrpcApi constructor(
    val route: Route,
    val negotiator: HttpServerNegotiator,
    val interceptors: List<Interceptor.Server>,
) : HdxServerEngine() {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: Interceptor.Server)
        @ExperimentalMmrpcApi
        fun install(negotiator: HttpServerNegotiator)
        fun routing(block: context(HttpServerEngine) () -> Unit)
    }

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        route.post(canonicalName) {
            val ctx = HttpServerContext(call, canonicalName)
            withContext(ctx) {
                val request = with(ctx) { negotiator.getRequest(reqSerial) }
                val foldReq = foldRequest(interceptors, canonicalName, request)
                handler(foldReq)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    override fun <Req : Any, Res : Any> register1(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Req) -> Res
    ) {
        route.post(canonicalName) {
            val ctx = HttpServerContext(call, canonicalName)
            withContext(ctx) {
                try {
                    val request = with(ctx) { negotiator.getRequest(reqSerial) }
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    val response = handler(foldReq)
                    val foldRes = foldResponse(interceptors, canonicalName, response)
                    with(ctx) { negotiator.setResponse(resSerial, foldRes) }
                } catch (e: FaultException) {
                    val error = e.toFaultObject()
                    val foldErr = foldError(interceptors, canonicalName, error)
                    with(ctx) { negotiator.setError(foldErr) }
                }
            }
        }
    }
}
