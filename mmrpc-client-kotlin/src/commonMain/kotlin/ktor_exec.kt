package org.cufy.mmrpc.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.JsonObject
import org.cufy.json.asContentStringOrNull
import org.cufy.json.serializeToJsonString
import org.cufy.mmrpc.*

suspend inline fun <reified I : StructObject, reified O : StructObject> HttpClient.exec(
    routine: RoutineObject<I, O>,
    input: I,
    token: String? = null,
    baseurl: String?,
    block: HttpRequestBuilder.() -> Unit = {}
): O {
    val endpoint = routine.__info__.endpoints.asSequence()
        .filterIsInstance<HttpEndpointInfo>()
        .firstOrNull()

    requireNotNull(endpoint) {
        "Routine does not have an Http endpoint"
    }

    val keyString = generateKey(routine, input)
    val valueString = input.serializeToJsonString()
    val method = endpoint.method.firstOrNull() ?: Http.POST

    try {
        return post {
            baseurl?.let { this.url.takeFrom(it) }

            this.method = HttpMethod.parse(method.name)
            this.url.appendPathSegments(endpoint.path.value)

            keyString?.let { this.headers["X-MMRPC-KEY"] = it }
            token?.let { bearerAuth(it) }

            contentType(ContentType.Application.Json)
            setBody(valueString)

            block()
        }.body()
    } catch (cause: ResponseException) {
        if (cause.response.status.value in 400..<600) {
            val obj = try {
                cause.response.body<JsonObject>()
            } catch (_: Exception) {
                throw cause
            }

            val canonicalNameString = obj["type"]?.asContentStringOrNull
            val message = obj["message"]?.asContentStringOrNull

            canonicalNameString ?: throw cause

            throw FaultException(CanonicalName(canonicalNameString), message, cause)
        }

        throw cause
    }
}
