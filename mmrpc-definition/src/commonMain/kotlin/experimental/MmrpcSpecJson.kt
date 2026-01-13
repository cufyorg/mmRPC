package org.cufy.mmrpc.experimental

import kotlinx.serialization.json.Json
import org.cufy.mmrpc.MmrpcSpec

private val FORMAT = Json {
    encodeDefaults = false
}

fun MmrpcSpec.toJsonString(): String {
    return FORMAT.encodeToString(this)
}

fun MmrpcSpec.Companion.fromJsonString(source: String): MmrpcSpec {
    return FORMAT.decodeFromString(source)
}
