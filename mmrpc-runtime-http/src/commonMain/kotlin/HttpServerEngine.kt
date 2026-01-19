package org.cufy.mmrpc.runtime.http

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.FaultException
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.ServerEngine

class HttpServerEngine(
    val route: Route,
    val contentNegotiator: HttpServerContentNegotiator,
    val interceptors: List<HttpServerInterceptor>,
) : ServerEngine.Http {
    interface Builder {
        fun install(interceptor: HttpServerInterceptor)
        fun install(negotiator: HttpServerContentNegotiator)
        fun routing(block: context(HttpServerEngine) () -> Unit)
    }

    override fun is0Supported() = true
    override fun is1Supported() = true

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        route.post(canonicalName) {
            val request = contentNegotiator.getReq(call, reqSerial)

            if (!interceptors.all { it.onReq(call, canonicalName, request) })
                return@post

            handler(request)
            call.respond(HttpStatusCode.OK)
        }
    }

    override fun <Req : Any, Res : Any> register1(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Req) -> Res
    ) {
        route.post(canonicalName) {
            val request = contentNegotiator.getReq(call, reqSerial)

            if (!interceptors.all { it.onReq(call, canonicalName, request) })
                return@post

            val response = try {
                handler(request)
            } catch (e: FaultException) {
                val error = FaultObject(
                    canonicalName = e.canonicalName,
                    message = e.message,
                )

                if (!interceptors.all { it.onErr(call, canonicalName, request, error) })
                    return@post

                contentNegotiator.setErr(call, error)
                return@post
            }

            if (!interceptors.all { it.onRes(call, canonicalName, request, response) })
                return@post

            contentNegotiator.setRes(call, resSerial, response)
        }
    }
}
