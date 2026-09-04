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
import org.cufy.mmrpc.runtime.Interceptor.Companion.foldException
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
            val result = wrapInCatch(ctx, canonicalName) {
                client.post {
                    ctx.request = this
                    this.url.appendPathSegments(canonicalName)
                    val foldReq = foldRequest(interceptors, canonicalName, request)
                    with(ctx) { negotiator.setRequest(reqSerial, foldReq) }
                }
            }

            ctx.response = result
            val response = with(ctx) { negotiator.getResponse(resSerial) }
            val foldRes = foldResponse(interceptors, canonicalName, response)
            foldRes
        }
    }

    private suspend inline fun <R> wrapInCatch(
        ctx: HttpClientContext,
        canonicalName: String,
        block: () -> R,
    ): R {
        try {
            return block()
        } catch (e: ResponseException) {
            ctx.response = e.response
            val error = with(ctx) { negotiator.getError() }
            if (error == null) {
                // [[ Fallback foldException ]] //
                val foldErr = foldException(interceptors, canonicalName, e) ?: throw e
                throw foldErr.toFaultException(e)
            }
            val foldErr = foldError(interceptors, canonicalName, error)
            throw foldErr.toFaultException(e)
        } catch (e: Throwable) {
            // [[ Fallback foldException ]] //
            val foldErr = foldException(interceptors, canonicalName, e) ?: throw e
            throw foldErr.toFaultException(e)
        }
    }
}
