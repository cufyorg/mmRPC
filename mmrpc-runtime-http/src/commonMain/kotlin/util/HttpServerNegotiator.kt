package org.cufy.mmrpc.runtime.http.util

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.http.HttpServerContext

interface HttpServerNegotiator {
    @ExperimentalMmrpcApi
    context(_: HttpServerContext)
    suspend fun <Req> getRequest(reqSerial: KSerializer<Req>): Req
    @ExperimentalMmrpcApi
    context(_: HttpServerContext)
    suspend fun <Res> setResponse(resSerial: KSerializer<Res>, response: Res)
    @ExperimentalMmrpcApi
    context(_: HttpServerContext)
    suspend fun setError(error: FaultObject)

    object Default : HttpServerNegotiator {
        @ExperimentalMmrpcApi
        context(ctx: HttpServerContext)
        override suspend fun <Req> getRequest(reqSerial: KSerializer<Req>): Req {
            return when (val type = ctx.call.request.header(HttpHeaders.ContentType)) {
                "application/json" -> {
                    val reqStr = ctx.call.receiveText()
                    Json.decodeFromString(reqSerial, reqStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }

        @ExperimentalMmrpcApi
        context(ctx: HttpServerContext)
        override suspend fun <Res> setResponse(resSerial: KSerializer<Res>, response: Res) {
            val resStr = Json.encodeToString(resSerial, response)
            ctx.call.response.header(HttpHeaders.ContentType, "application/json")
            ctx.call.respond(HttpStatusCode.OK, resStr)
        }

        @ExperimentalMmrpcApi
        context(ctx: HttpServerContext)
        override suspend fun setError(error: FaultObject) {
            val errStr = Json.encodeToString(error)
            ctx.call.response.header(HttpHeaders.ContentType, "application/json")
            ctx.call.respond(HttpStatusCode.BadRequest, errStr)
        }
    }
}
