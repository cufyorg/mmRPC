package org.cufy.mmrpc.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.cufy.json.serializeToJsonString
import org.cufy.mmrpc.Http
import org.cufy.mmrpc.HttpEndpointInfo
import org.cufy.mmrpc.RoutineObject
import org.cufy.mmrpc.StructObject

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
}
