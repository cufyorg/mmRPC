package org.cufy.mmrpc.runtime.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.HdxClientEngine
import org.cufy.mmrpc.runtime.Interceptor
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldError
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldRequest
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldResponse
import org.cufy.mmrpc.runtime.http.util.HttpClientNegotiator
import org.cufy.mmrpc.runtime.toFaultException

@OptIn(ExperimentalMmrpcApi::class)
class HttpClientEngine @ExperimentalMmrpcApi constructor(
    val client: HttpClient,
    val negotiator: HttpClientNegotiator,
    val interceptors: List<Interceptor.Client>,
) : HdxClientEngine() {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: Interceptor.Client)
        @ExperimentalMmrpcApi
        fun install(negotiator: HttpClientNegotiator)
    }

    override suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    ) {
        val ctx = HttpClientContext(canonicalName)
        withContext(ctx) {
            client.post {
                ctx.request = this
                this.url.appendPathSegments(canonicalName)
                val foldReq = foldRequest(interceptors, canonicalName, request)
                with(ctx) { negotiator.setRequest(reqSerial, foldReq) }
            }
        }
    }

    override suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res {
        val ctx = HttpClientContext(canonicalName)
        return withContext(ctx) {
            val result = try {
                client.post {
                    ctx.request = this
                    this.url.appendPathSegments(canonicalName)
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    with(ctx) { negotiator.setRequest(reqSerial, foldReq) }
                }
            } catch (cause: ResponseException) {
                ctx.response = cause.response
                val error = with(ctx) { negotiator.getError() } ?: throw cause
                val foldErr = foldError(interceptors, canonicalName, error)
                throw foldErr.toFaultException(cause)
            }

            ctx.response = result
            val response = with(ctx) { negotiator.getResponse(resSerial) }
            val foldRes = foldResponse(interceptors, canonicalName, response)
            foldRes
        }
    }
}
