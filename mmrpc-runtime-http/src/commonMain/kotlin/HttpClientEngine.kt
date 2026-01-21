package org.cufy.mmrpc.runtime.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.ExperimentalMmrpcApi
import org.cufy.mmrpc.runtime.FaultException

@OptIn(ExperimentalMmrpcApi::class)
class HttpClientEngine @ExperimentalMmrpcApi constructor(
    val client: HttpClient,
    val contentNegotiator: HttpClientContentNegotiator,
    val interceptors: List<HttpClientInterceptor>,
) : ClientEngine.Http {
    interface Builder {
        @ExperimentalMmrpcApi
        fun install(interceptor: HttpClientInterceptor)
        @ExperimentalMmrpcApi
        fun install(negotiator: HttpClientContentNegotiator)
    }

    override fun is0Supported() = true
    override fun is1Supported() = true

    override suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    ) {
        client.post {
            this.url.appendPathSegments(canonicalName)
            contentNegotiator.setReq(this, reqSerial, request)
            interceptors.forEach { it.onReq(this, canonicalName, request) }
        }
    }

    override suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res {
        val result = try {
            client.post {
                this.url.appendPathSegments(canonicalName)
                contentNegotiator.setReq(this, reqSerial, request)
                interceptors.forEach { it.onReq(this, canonicalName, request) }
            }
        } catch (cause: ResponseException) {
            if (cause.response.status.value in 400..<600) {
                val error = try {
                    contentNegotiator.getErr(cause.response)
                        ?: throw cause
                } catch (_: Exception) {
                    throw cause
                }

                interceptors.forEach { it.onErr(cause.response, canonicalName, request, error) }

                throw FaultException(
                    canonicalName = error.canonicalName,
                    message = error.message,
                    cause = cause,
                )
            }

            throw cause
        }

        val response = contentNegotiator.getRes(result, resSerial)
        interceptors.forEach { it.onRes(result, canonicalName, request, response) }
        return response
    }
}
