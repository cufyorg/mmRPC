package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a half-duplex server engine for handling remote procedure calls (RPC).
 *
 * The `Hdx` interface extends the [ServerEngine] interface, providing capabilities specific
 * to half-duplex communication. In half-duplex communication, data transmission occurs
 * in one direction at a time, alternating between sending and receiving.
 *
 * This interface should be implemented to support RPC mechanisms that require such
 * communication patterns.
 */
abstract class HdxServerEngine : ServerEngine {
    final override fun is0Supported() = true
    final override fun is1Supported() = true
    final override fun is2Supported() = false
    final override fun is3Supported() = false
    final override fun is4Supported() = false

    final override fun <Req : Any, Res : Any> register2(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Flow<Req>) -> Res,
    ) = error("Hdx doesn't support: (Flow<Req>) -> Res")

    final override fun <Req : Any, Res : Any> register3(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: (Req) -> Flow<Res>,
    ) = error("Hdx doesn't support exec: (Req) -> Flow<Res>")

    final override fun <Req : Any, Res : Any> register4(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: (Flow<Req>) -> Flow<Res>,
    ) = error("Hdx doesn't support exec: (Flow<Req>) -> Flow<Res>")
}
