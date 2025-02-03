package org.cufy.mmrpc.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import org.cufy.json.asContentStringOrNull
import org.cufy.json.serializeToJsonString
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.Comm
import org.cufy.mmrpc.FaultException
import org.cufy.mmrpc.RoutineObject

suspend inline fun <reified I : Any, reified O : Any> HttpClient.exec(
    routine: RoutineObject<I, O>,
    input: I,
    token: String? = null,
    baseurl: String?,
): O {
    require(Comm.Http in routine.comm) {
        "Routine does not support Http communication channel"
    }

    val path = routine.canonicalName.value
    val value = input.serializeToJsonString()

    try {
        return post {
            baseurl?.let { this.url.takeFrom(it) }

            this.method = HttpMethod.Post
            this.url.appendPathSegments(path)

            token?.let { bearerAuth(it) }

            contentType(ContentType.Application.Json)
            setBody(value)
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
