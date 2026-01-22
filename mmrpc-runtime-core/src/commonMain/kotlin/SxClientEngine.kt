package org.cufy.mmrpc.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Represents a simplex operational mode client engine.
 *
 * This interface is a subtype of [ClientEngine] that defines a communication
 * pattern where data flows in a single direction, either as requests or
 * responses, but not simultaneously in both directions.
 *
 * Implementations of this interface are expected to adhere to the simplex
 * communication model, ensuring data flow is restricted to one direction
 * per interaction.
 */
abstract class SxClientEngine : ClientEngine {
    final override fun is0Supported() = true
    final override fun is1Supported() = false
    final override fun is2Supported() = false
    final override fun is3Supported() = false
    final override fun is4Supported() = false

    final override suspend fun <Req : Any, Res : Any> exec1(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res = error("Sx doesn't support: (Req) -> Res")

    final override suspend fun <Req : Any, Res : Any> exec2(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Res = error("Sx doesn't support: (Flow<Req>) -> Res")

    final override fun <Req : Any, Res : Any> exec3(
        canonicalName: String,
        request: Req,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res> = error("Sx doesn't support exec: (Req) -> Flow<Res>")

    final override fun <Req : Any, Res : Any> exec4(
        canonicalName: String,
        request: Flow<Req>,
        reqSerial: KSerializer<Req>,
        resSerial: KSerializer<Res>,
    ): Flow<Res> = error("Sx doesn't support exec: (Flow<Req>) -> Flow<Res>")
}
