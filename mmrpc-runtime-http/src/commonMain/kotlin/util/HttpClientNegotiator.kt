package org.cufy.mmrpc.runtime.http.util

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultObject
import org.cufy.mmrpc.runtime.http.HttpClientContext

interface HttpClientNegotiator {
    @ExperimentalMmrpcApi
    context(_: HttpClientContext)
    suspend fun <Req> setRequest(reqSerial: KSerializer<Req>, request: Req)
    @ExperimentalMmrpcApi
    context(_: HttpClientContext)
    suspend fun <Res> getResponse(resSerial: KSerializer<Res>): Res
    @ExperimentalMmrpcApi
    context(_: HttpClientContext)
    suspend fun getError(): FaultObject?

    object Default : HttpClientNegotiator {
        @ExperimentalMmrpcApi
        context(ctx: HttpClientContext)
        override suspend fun <Req> setRequest(reqSerial: KSerializer<Req>, request: Req) {
            val reqStr = Json.encodeToString(reqSerial, request)
            ctx.request.header(HttpHeaders.ContentType, "application/json")
            ctx.request.setBody(reqStr)
        }

        @ExperimentalMmrpcApi
        context(ctx: HttpClientContext)
        override suspend fun <Res> getResponse(resSerial: KSerializer<Res>): Res {
            when (val type = ctx.response.headers[HttpHeaders.ContentType]) {
                "application/json" -> {
                    val reqStr = ctx.response.bodyAsText()
                    return Json.decodeFromString(resSerial, reqStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }

        @ExperimentalMmrpcApi
        context(ctx: HttpClientContext)
        override suspend fun getError(): FaultObject? {
            return when (ctx.response.headers[HttpHeaders.ContentType]) {
                "application/json" -> {
                    val errStr = ctx.response.bodyAsText()
                    Json.decodeFromString<FaultObject>(errStr)
                }

                else -> null
            }
        }
    }
}
