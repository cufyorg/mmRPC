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
    val contentNegotiator: HttpServerContentNegotiator =
        HttpServerContentNegotiator.Default,
) : ServerEngine.Http {
    override fun is0Supported() = true
    override fun is1Supported() = true

    override fun <Req : Any> register0(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        handler: suspend (Req) -> Unit
    ) {
        route.post(canonicalName) {
            val request = contentNegotiator.getReq(call, reqSerial)
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

            val response = try {
                handler(request)
            } catch (e: FaultException) {
                val error = FaultObject(
                    canonicalName = e.canonicalName,
                    message = e.message,
                )

                contentNegotiator.setErr(call, error)
                return@post
            }

            contentNegotiator.setRes(call, resSerial, response)
        }
    }
}
