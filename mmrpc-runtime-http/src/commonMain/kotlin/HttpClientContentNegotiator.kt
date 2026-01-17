package org.cufy.mmrpc.runtime.http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.cufy.mmrpc.runtime.FaultObject

interface HttpClientContentNegotiator {
    suspend fun <Req> setReq(ctx: HttpRequestBuilder, reqSerial: KSerializer<Req>, request: Req)
    suspend fun <Res> getRes(ctx: HttpResponse, resSerial: KSerializer<Res>): Res
    suspend fun getErr(ctx: HttpResponse): FaultObject

    object Default : HttpClientContentNegotiator {
        override suspend fun <Req> setReq(
            ctx: HttpRequestBuilder,
            reqSerial: KSerializer<Req>,
            request: Req
        ) {
            val reqStr = Json.encodeToString(reqSerial, request)
            ctx.header(HttpHeaders.ContentType, "application/json")
            ctx.setBody(reqStr)
        }

        override suspend fun <Res> getRes(
            ctx: HttpResponse,
            resSerial: KSerializer<Res>
        ): Res {
            when (val type = ctx.headers[HttpHeaders.ContentType]) {
                "application/json" -> {
                    val reqStr = ctx.bodyAsText()
                    return Json.decodeFromString(resSerial, reqStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }

        override suspend fun getErr(ctx: HttpResponse): FaultObject {
            when (val type = ctx.headers[HttpHeaders.ContentType]) {
                "application/json" -> {
                    val errStr = ctx.bodyAsText()
                    return Json.decodeFromString<FaultObject>(errStr)
                }

                else -> error("Unsupported Content Type: $type")
            }
        }
    }
}
