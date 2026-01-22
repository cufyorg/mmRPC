package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a client engine for executing remote procedure calls (RPC) with different modes
 * of data transmission (simplex, half-duplex, full-duplex).
 *
 * The [ClientEngine] interface provides methods for executing various types of RPC calls
 * (unary, stream-based, etc.) with serialization and deserialization capabilities.
 */
sealed interface ClientEngine {
    companion object

    fun is0Supported(): Boolean
    fun is1Supported(): Boolean
    fun is2Supported(): Boolean
    fun is3Supported(): Boolean
    fun is4Supported(): Boolean

    suspend fun <Req : Any> exec0(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
    )

    suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res

    suspend fun <Req : Any, Res : Any> exec2(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res

    fun <Req : Any, Res : Any> exec3(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res>

    fun <Req : Any, Res : Any> exec4(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res>
}
