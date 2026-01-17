package org.cufy.mmrpc.runtime.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.runtime.FaultObject

interface HttpServerContentNegotiator {
    suspend fun <Req> getReq(ctx: ApplicationCall, reqSerial: KSerializer<Req>): Req
    suspend fun <Res> setRes(ctx: ApplicationCall, resSerial: KSerializer<Res>, response: Res)
    suspend fun setErr(ctx: ApplicationCall, error: FaultObject)

    object Default : HttpServerContentNegotiator {
        override suspend fun <Req> getReq(ctx: ApplicationCall, reqSerial: KSerializer<Req>): Req {
            return when (val type = ctx.request.header(HttpHeaders.ContentType)) {
                "application/json" -> {
                    val reqStr = ctx.receiveText()
                    Json.decodeFromString(reqSerial, reqStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }

        override suspend fun <Res> setRes(ctx: ApplicationCall, resSerial: KSerializer<Res>, response: Res) {
            val resStr = Json.encodeToString(resSerial, response)
            ctx.response.header(HttpHeaders.ContentType, "application/json")
            ctx.respond(HttpStatusCode.OK, resStr)
        }

        override suspend fun setErr(ctx: ApplicationCall, error: FaultObject) {
            val errStr = Json.encodeToString(error)
            ctx.response.header(HttpHeaders.ContentType, "application/json")
            ctx.respond(HttpStatusCode.BadRequest, errStr)
        }
    }
}
