package org.cufy.mmrpc.runtime.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import org.cufy.mmrpc.runtime.ClientEngine
import org.cufy.mmrpc.runtime.FaultException

class HttpClientEngine(
    val client: HttpClient,
    val contentNegotiator: HttpClientContentNegotiator =
        HttpClientContentNegotiator.Default
) : ClientEngine.Http {
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
        }
    }

    override suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res {
        try {
            val result = client.post {
                this.url.appendPathSegments(canonicalName)
                contentNegotiator.setReq(this, reqSerial, request)
            }

            val response = contentNegotiator.getRes(result, resSerial)
            return response
        } catch (cause: ResponseException) {
            if (cause.response.status.value in 400..<600) {
                val error = try {
                    contentNegotiator.getErr(cause.response)
                } catch (_: Exception) {
                    throw cause
                }

                throw FaultException(
                    canonicalName = error.canonicalName,
                    message = error.message,
                    cause = cause,
                )
            }

            throw cause
        }
    }
}
