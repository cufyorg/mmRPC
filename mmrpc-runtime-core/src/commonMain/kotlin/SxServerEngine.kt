package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a simplex server engine for handling remote procedure calls (RPC) with single-directional
 * communication. It provides mechanisms to register routines for various modes of data processing
 * (unary, streaming, etc.).
 *
 * This interface extends [ServerEngine], inheriting its capabilities for handling RPC requests
 * and responses while focusing on simplex communication patterns.
 */
abstract class SxServerEngine : ServerEngine {
    final override fun is0Supported() = true
    final override fun is1Supported() = false
    final override fun is2Supported() = false
    final override fun is3Supported() = false
    final override fun is4Supported() = false

    final override fun <Req : Any, Res : Any> register1(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Req) -> Res,
    ) = error("Sx doesn't support: (Req) -> Res")

    final override fun <Req : Any, Res : Any> register2(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: suspend (Flow<Req>) -> Res,
    ) = error("Sx doesn't support: (Flow<Req>) -> Res")

    final override fun <Req : Any, Res : Any> register3(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: (Req) -> Flow<Res>,
    ) = error("Sx doesn't support exec: (Req) -> Flow<Res>")

    final override fun <Req : Any, Res : Any> register4(
        canonicalName: String,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
        handler: (Flow<Req>) -> Flow<Res>,
    ) = error("Sx doesn't support exec: (Flow<Req>) -> Flow<Res>")
}
