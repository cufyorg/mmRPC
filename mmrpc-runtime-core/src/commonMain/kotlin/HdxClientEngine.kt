package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a Half-Duplex client engine.
 *
 * This interface is designed for implementing client engines that follow
 * a half-duplex communication model. Half-duplex communication allows data
 * to flow in one direction at a time, meaning that sending and receiving
 * operations cannot occur simultaneously.
 *
 * It extends the [ClientEngine] interface, providing the necessary foundation
 * for handling various client-side communication mechanisms, such as executing
 * requests and managing streams.
 *
 * The implementing class should specify the supported modes and handle
 * corresponding execution logic for one-way or bidirectional communication
 * patterns within the constraints of a half-duplex operation.
 */
abstract class HdxClientEngine : ClientEngine {
    final override fun is0Supported() = true
    final override fun is1Supported() = true
    final override fun is2Supported() = false
    final override fun is3Supported() = false
    final override fun is4Supported() = false

    final override suspend fun <Req : Any, Res : Any> exec2(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res = error("Hdx doesn't support: (Flow<Req>) -> Res")

    final override fun <Req : Any, Res : Any> exec3(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res> = error("Hdx doesn't support exec: (Req) -> Flow<Res>")

    final override fun <Req : Any, Res : Any> exec4(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res> = error("Hdx doesn't support exec: (Flow<Req>) -> Flow<Res>")
}
