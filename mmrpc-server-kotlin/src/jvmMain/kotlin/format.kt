package org.cufy.mmrpc.server

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.reflect.KType

@PublishedApi
internal val json = Json { isLenient = true; ignoreUnknownKeys = true }

@Suppress("UNCHECKED_CAST")
private fun <T> Json.serializer(type: KType): KSerializer<T> =
    serializersModule.serializer(type) as KSerializer<T>

internal fun <T> String.deserializeJsonCatchingUnsafe(type: KType, json: Json = Json): Result<T> {
    return try {
        success(json.decodeFromString(json.serializer(type), this))
    } catch (e: SerializationException) {
        failure(e)
    } catch (e: IllegalArgumentException) {
        failure(e)
    }
}

internal fun <T> T.deserializeToJsonStringUnsafe(type: KType, json: Json = Json): String {
    return json.encodeToString(json.serializer(type), this)
}
